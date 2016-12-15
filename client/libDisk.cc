#include "libDisk.h"

#include <iostream>
#include <map>
#include <stdint.h>
#include <stdlib.h>
#include <string>

#include "easywsclient.hpp"
#include "model.pb.h"

#define MAX_DISKS 100000
#define MAX_TIMEOUTS 3
#define WEBSOCKET_TIMEOUT_MS 10000
#ifdef LIBDISK_DEBUG
#define LOG(message) std::cout << "[libDisk " << __FUNCTION__ << "] " << message << std::endl;
#else
#define LOG(message) do {} while (0)
#endif
#define LOGERR(message) std::cerr << "[libDisk " << __FUNCTION__ << "] " << message << std::endl;
#define CHECK_WEBSOCKET()                                                      \
  if (!g_websocket) {                                                          \
    LOGERR("WebSocket not initialized");                                       \
    return LIBDISK_WEBSOCKET_NOT_INITIALIZED;                                  \
  }                                                                            \
  if (g_websocket->getReadyState() != WebSocket::OPEN) {                       \
    return LIBDISK_WEBSOCKET_DISCONNECTED;                                     \
  }

using easywsclient::WebSocket;

struct Disk {
  std::string name;
  // TODO support disk size
  //int nBytes;
};

typedef std::map<int, Disk> DiskMap;

static WebSocket::pointer g_websocket = NULL;

static bool g_request_returned = false;
static std::string g_websocket_message;

static std::string g_session_id;
static DiskMap g_id_to_disk;
static int g_next_free_disk_id = 0;

enum PollWebSocketResponse {
  SUCCESS,
  DISCONNECTED,
  TIMED_OUT
};

template <typename T>
std::string SerializeRequest(T inner_protobuf) {
  tinyfs::ClientRequest request_protobuf;
  request_protobuf.mutable_request()->PackFrom(inner_protobuf);
  std::string serialized_request;
  request_protobuf.SerializeToString(&serialized_request);
  return serialized_request;
}

static void WebSocketCallback(const std::string& message) {
  g_websocket_message = message;
  g_request_returned = true;
}

static PollWebSocketResponse SendAndReceive(const std::string send_data) {
  g_websocket->sendBinary(send_data);
  for (int i = 0; i < MAX_TIMEOUTS || i < 2; i++) {
    // poll() may need to be called multiple times. It will block once
    // for WEBSOCKET_TIMEOUT_MS during each call until either the socket
    // can be written to or read from. It will normally unblock instantly
    // and write data from sendBinary(), and read nothing. poll() will then
    // need to be called again in order to block for reading, and then
    // reading will actually occur. In case reading and writing both
    // happen in one call to poll(), g_request_returned will be true after
    // dispatch() is called.
    g_websocket->poll(WEBSOCKET_TIMEOUT_MS);
    g_websocket->dispatch(WebSocketCallback);

    if (g_websocket->getReadyState() != WebSocket::OPEN) {
      return DISCONNECTED;
    } else if (g_request_returned) {
      g_request_returned = false;
      return SUCCESS;
    }
  }

  return TIMED_OUT;
}

/**
 * Initializes the remote disk
 */
int initLibDisk(char* address, char* token) {
  g_websocket = WebSocket::from_url(address);
  CHECK_WEBSOCKET();

  tinyfs::ClientRegistrationRequest request;
  request.set_token(token);

  PollWebSocketResponse response = SendAndReceive(SerializeRequest(request));
  switch (response) {
    case SUCCESS:
      LOG("successfully connected to \"" << address << "\" with session id \"" << g_session_id << "\"");
      break;
    case DISCONNECTED:
      LOGERR("Disconnected from websocket");
      return LIBDISK_WEBSOCKET_TIMED_OUT;
    case TIMED_OUT:
      LOGERR("WebSocket connection timed out");
      return LIBDISK_WEBSOCKET_DISCONNECTED;
  }

  tinyfs::ClientRegistrationResponse registration_response;
  if (!registration_response.ParseFromString(g_websocket_message)) {
    LOGERR("Failed to parse data read from websocket");
    return LIBDISK_WEBSOCKET_BAD_DATA;
  }

  g_session_id = registration_response.sessionid();

  return LIBDISK_SUCCESS;
}

/**
 * The emulator is a library of three functions that operate on a regular UNIX
 * file. The necessary functions are: openDisk(), readBlock(), writeBlock().
 * There is also a single piece of data that is required: BLOCKSIZE, the size
 * of a disk block in bytes. This should be statically defined to 256 bytes
 * using a macro (see below). All IO done to the emulated disk must be block
 * aligned to BLOCKSIZE, meaning that the disk assumes the buffers passed in
 * readBlock() and writeBlock() are exactly BLOCKSIZE bytes large. If they are
 * not, the behavior is undefined.
 */

/**
 * This functions opens a regular UNIX file and designates the first nBytes of
 * it as space for the emulated disk. If nBytes is not exactly a multiple of
 * BLOCKSIZE then the disk size will be the closest multiple of BLOCKSIZE that
 * is lower than nByte (but greater than 0) If nBytes is less than BLOCKSIZE
 * failure should be returned. If nBytes > BLOCKSIZE and there is already a
 * file by the given filename, that file’s content may be overwritten. If
 * nBytes is 0, an existing disk is opened, and should not be overwritten.
 * There is no requirement to maintain integrity of any file content beyond
 * nBytes. The return value is -1 on failure or a disk number on success.
 */
int openDisk(char* filename, int nBytes) {
  CHECK_WEBSOCKET();

  if (g_next_free_disk_id > MAX_DISKS) {
    return LIBDISK_TOO_MANY_DISKS_OPEN;
  }

  Disk new_disk;
  new_disk.name = filename;

  int disk_id = g_next_free_disk_id++;
  g_id_to_disk[disk_id] = new_disk;

  return disk_id;
}

/**
 * readBlock() reads an entire block of BLOCKSIZE bytes from the open disk
 * (identified by ‘disk’) and copies the result into a local buffer (must be at
 * least of BLOCKSIZE bytes). The bNum is a logical block number, which must be
 * translated into a byte offset within the disk. The translation from logical
 * to physical block is straightforward: bNum=0 is the very first byte of the
 * file. bNum=1 is BLOCKSIZE bytes into the disk, bNum=n is n*BLOCKSIZE bytes
 * into the disk. On success, it returns 0. -1 or smaller is returned if disk
 * is not available (hasn’t been opened) or any other failures. You must define
 * your own error code system.
 */
int readBlock(int disk_id, int bNum, void* block) {
  DiskMap::iterator disk_iterator = g_id_to_disk.find(disk_id);
  if (disk_iterator == g_id_to_disk.end()) {
    return LIBDISK_INVALID_DISK;
  }
  Disk disk = disk_iterator->second;

  tinyfs::ReadRequest request;
  request.set_sessionid(g_session_id);
  request.set_file(disk.name);
  request.set_offset(BLOCKSIZE * bNum);
  request.set_size(BLOCKSIZE);

  PollWebSocketResponse response = SendAndReceive(SerializeRequest(request));
  switch (response) {
    case SUCCESS:
      break;
    case DISCONNECTED:
      LOGERR("Disconnected from websocket");
      return LIBDISK_WEBSOCKET_TIMED_OUT;
    case TIMED_OUT:
      LOGERR("WebSocket connection timed out");
      return LIBDISK_WEBSOCKET_DISCONNECTED;
  }

  tinyfs::ReadResponse read_response;
  if (!read_response.ParseFromString(g_websocket_message)) {
    LOGERR("Failed to parse data read from websocket");
    return LIBDISK_WEBSOCKET_BAD_DATA;
  }

  const std::string read_block = read_response.message();
  memcpy(block, read_block.data(), BLOCKSIZE);

  return LIBDISK_SUCCESS;
}

/**
 * writeBlock() takes disk number ‘disk’ and logical block number ‘bNum’ and
 * writes the content of the buffer ‘block’ to that location. ‘block’ must be
 * integral with BLOCKSIZE. Just as in readBlock(), writeBlock() must translate
 * the logical block bNum to the correct byte position in the file. On success,
 * it returns 0. -1 or smaller is returned if disk is not available
 * (i.e. hasn’t been opened) or any other failures. You must define your own
 * error code system.
 */
int writeBlock(int disk_id, int bNum, void* block) {
  DiskMap::iterator disk_iterator = g_id_to_disk.find(disk_id);
  if (disk_iterator == g_id_to_disk.end()) {
    return LIBDISK_INVALID_DISK;
  }
  Disk disk = disk_iterator->second;

  tinyfs::WriteRequest request;
  request.set_sessionid(g_session_id);
  request.set_file(disk.name);
  request.set_message(block, BLOCKSIZE);
  request.set_offset(BLOCKSIZE * bNum);

  PollWebSocketResponse response = SendAndReceive(SerializeRequest(request));
  switch (response) {
    case SUCCESS:
      break;
    case DISCONNECTED:
      LOGERR("Disconnected from websocket");
      return LIBDISK_WEBSOCKET_TIMED_OUT;
    case TIMED_OUT:
      LOGERR("WebSocket connection timed out");
      return LIBDISK_WEBSOCKET_DISCONNECTED;
  }

  // WriteResponse is empty, do nothing else here

  return LIBDISK_SUCCESS;
}

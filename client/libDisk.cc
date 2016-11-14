#include "libDisk.h"

#include <iostream>
#include <map>
#include <stdint.h>
#include <stdlib.h>
#include <string>

#include "easywsclient.hpp"
#include "model.pb.h"

#define WEBSOCKET_TIMEOUT_MS 10000

using easywsclient::WebSocket;

static const char websocket_address[] = "ws://home.arhar.net:4880/client";
// TODO remove this temporary token
static const char token[] = "I2gb1RHVrx1P8kJCNMx9Rw==";

static WebSocket::pointer g_websocket = NULL;
static bool g_registration_request_returned = false;
static bool g_read_request_returned = false;
static std::string g_session_id;

static std::map<int, std::string> disk_id_to_name;
static int next_free_disk_id = 0;

static void RegistrationCallback(const std::string& message) {
  tinyfs::ClientRegistrationResponse response;
  if (response.ParseFromString(message)) {
    // successfully parsed message
    std::cout << __FUNCTION__ << " \"" << response.sessionid() << "\"" << std::endl;
    g_session_id = response.sessionid();
  } else {
    std::cout << __FUNCTION__ << " failed to parse ClientRegistrationResponse" << std::endl;
  }
  g_registration_request_returned = true;
}

static void ReadRequestCallback(const std::string& message) {
  tinyfs::ReadResponse response;
  if (response.ParseFromString(message)) {
    std::cout << __FUNCTION__ << " \"" << response.message() << "\"" << std::endl;
  } else {
    std::cout << __FUNCTION__ << " failed to parse ReadResponse" << std::endl;
  }
  g_read_request_returned = true;
}

/**
 * The emulator is a library of three functions that operate on a regular UNIX
 * file. The necessary functions are: openDisk(), readBlock(), writeBlock().
 * There is also a single piece of data that is required: BLOCKSIZE, the size
 * of a disk block in bytes. This should be statically defined to 256 bytes
 * using a macro (see below). All IO done to the emulated disk must be block
 * aligned to BLOCKSIZE, meaning that the disk assumes the buffers passed in
 * readBlock() and writeBlock()are exactly BLOCKSIZE bytes large. If they are
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
  g_websocket = WebSocket::from_url(websocket_address);
  if (!g_websocket) {
    std::cout << "!websocket, exiting" << std::endl;
    return 1;
  }

  // TODO move this to RegistrationCallback?
  // what if opening the disk fails, or this filename is already open?
  disk_id_to_name[next_free_disk_id++] = filename;

  tinyfs::ClientRegistrationRequest registration_protobuf;
  tinyfs::ClientRequest registration_request_protobuf;
  registration_protobuf.set_token(token);
  registration_protobuf.add_fs_names(filename);
  registration_request_protobuf.mutable_request()->PackFrom(registration_protobuf);
  std::string protobuf_string;
  registration_request_protobuf.SerializeToString(&protobuf_string);
  g_websocket->send(protobuf_string);

  std::cout << "sent message" << std::endl;

  while (!g_registration_request_returned) {
    std::cout << "polling" << std::endl;
    g_websocket->poll(WEBSOCKET_TIMEOUT_MS);
    g_websocket->dispatch(RegistrationCallback);
  }
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
int readBlock(int disk, int bNum, void* block) {
  // TODO check to make sure this is valid
  std::string fs_name = disk_id_to_name[disk];

  tinyfs::ReadRequest read_protobuf;
  tinyfs::ClientRequest read_request_protobuf;
  std::string protobuf_string;
  read_protobuf.set_sessionid(g_session_id);
  read_protobuf.set_filesystem(fs_name);
  read_protobuf.set_offset(0);
  read_protobuf.set_size(bNum);
  read_request_protobuf.mutable_request()->PackFrom(read_protobuf);
  read_request_protobuf.SerializeToString(&protobuf_string);
  g_websocket->send(protobuf_string);

  //while (websocket->getReadyState() != easywsclient::WebSocket::CLOSED) {
  while(!g_read_request_returned) {
    std::cout << "polling for read request callback" << std::endl;
    g_websocket->poll(WEBSOCKET_TIMEOUT_MS);
    g_websocket->dispatch(ReadRequestCallback);
  }
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
int writeBlock(int disk, int bNum, void* block) {
  // TODO check to make sure this is valid, move to function?
  std::string fs_name = disk_id_to_name[disk];

  tinyfs::WriteRequest write_protobuf;
  tinyfs::ClientRequest write_request_protobuf;
  std::string protobuf_string;
  write_protobuf.set_sessionid(g_session_id);
  write_protobuf.set_filesystem(fs_name);
  write_protobuf.set_message((char*) block);
  write_request_protobuf.mutable_request()->PackFrom(write_protobuf);
  write_request_protobuf.SerializeToString(&protobuf_string);
  g_websocket->send(protobuf_string);
  // TODO should we block for a response when sending write requests? yeah, probably.
  
  g_websocket->poll(WEBSOCKET_TIMEOUT_MS); // ?
}

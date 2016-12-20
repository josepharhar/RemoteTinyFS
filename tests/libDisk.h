#ifndef LIBDISK_H
#define LIBDISK_H

#ifdef __cplusplus
extern "C" {
#endif

// size of each disk block in bytes
#define BLOCKSIZE 256

// libDisk return codes
#define LIBDISK_SUCCESS 0
// -1X error codes - user error
#define LIBDISK_WEBSOCKET_NOT_INITIALIZED -10
#define LIBDISK_TOO_MANY_DISKS_OPEN -11
#define LIBDISK_INVALID_DISK -12
#define LIBDISK_INVALID_TOKEN -13
// -2X error codes - regularly encounterable errors
#define LIBDISK_WEBSOCKET_DISCONNECTED -20
#define LIBDISK_WEBSOCKET_TIMED_OUT -21
// -3X error codes - should never happen
#define LIBDISK_WEBSOCKET_BAD_DATA -31


/**
 * Initializes the remote disk. This must be called before any other methods
 * in this file. In a local libDisk implementation, the body of this method
 * should be empty and simply return LIBDISK_SUCCESS.
 */
int initLibDisk(char *token);

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
int openDisk(char *filename, int nBytes);

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
int readBlock(int disk, int bNum, void *block);

/**
 * writeBlock() takes disk number ‘disk’ and logical block number ‘bNum’ and
 * writes the content of the buffer ‘block’ to that location. ‘block’ must be
 * integral with BLOCKSIZE. Just as in readBlock(), writeBlock() must translate
 * the logical block bNum to the correct byte position in the file. On success,
 * it returns 0. -1 or smaller is returned if disk is not available
 * (i.e. hasn’t been opened) or any other failures. You must define your own
 * error code system.
 */
int writeBlock(int disk, int bNum, void *block);

#ifdef __cplusplus
}
#endif

#endif

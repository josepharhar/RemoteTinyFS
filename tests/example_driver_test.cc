#include "test.h"

#include <stdlib.h>
#include <string.h>

int main(int argc, char** argv) {

  char* token = argv[1];
  INFO("Using token \"" << token << "\"");

  int return_code;

  return_code = initLibDisk(token);
  if (return_code) {
    FAIL("initLibDisk() returned " << return_code);
  }
  INFO("Successfully connected to server");

  int disk = openDisk("testDisk", BLOCKSIZE * 3);
  if (disk < 0) {
    FAIL("openDisk() returned " << disk);
  }
  INFO("Successfully opened disk with id: " << disk);

  unsigned char* test_write_data = (unsigned char*) malloc(BLOCKSIZE * sizeof(char));
  unsigned char* test_read_data = (unsigned char*) malloc(BLOCKSIZE * sizeof(char));
  int i;
  for (i = 0; i < BLOCKSIZE; i++) {
    test_write_data[i] = (unsigned char) i;
  }

  return_code = writeBlock(disk, 0, test_write_data);
  if (return_code) {
    FAIL("writeBlock() returned " << return_code);
  }
  INFO("Successfully wrote data with writeBlock()");

  return_code = readBlock(disk, 0, test_read_data);
  if (return_code) {
    FAIL("readBlock() returned " << return_code);
  }
  INFO("Successfully read data with readBlock()");

  if (memcmp(test_write_data, test_read_data, BLOCKSIZE)) {
    FAIL("data read from readBlock() does not match");
  }
  INFO("Success: data read from readBlock() matches data written");

  return EXIT_SUCCESS;
}

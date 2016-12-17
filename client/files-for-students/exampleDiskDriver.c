#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "libDisk.h"

char token[] = YOUR_TOKEN_HERE;

int main(int argc, char** argv) {

  int return_code;

  return_code = initLibDisk(token);
  if (return_code) {
    printf("Failed: initLibDisk() returned %d\n", return_code);
    return EXIT_FAILURE;
  }
  printf("Successfully connected to server\n");

  int disk = openDisk("testDisk", BLOCKSIZE * 3);
  if (disk < 0) {
    printf("Failed: openDisk() returned %d\n", disk);
    return EXIT_FAILURE;
  }
  printf("Successfully opened disk with id: %d\n", disk);

  unsigned char* test_write_data = (unsigned char*) malloc(BLOCKSIZE * sizeof(char));
  unsigned char* test_read_data = (unsigned char*) malloc(BLOCKSIZE * sizeof(char));
  int i;
  for (i = 0; i < BLOCKSIZE; i++) {
    test_write_data[i] = (unsigned char) i;
  }

  return_code = writeBlock(disk, 0, test_write_data);
  if (return_code) {
    printf("Failed: writeBlock() returned %d\n", return_code);
    return EXIT_FAILURE;
  }
  printf("Successfully wrote data with writeBlock()\n");

  return_code = readBlock(disk, 0, test_read_data);
  if (return_code) {
    printf("Failed: readBlock() returned %d\n", return_code);
    return EXIT_FAILURE;
  }
  printf("Successfully read data with readBlock()\n");

  if (memcmp(test_write_data, test_read_data, BLOCKSIZE)) {
    printf("Failed: data read from readBlock() does not match\n");
    return EXIT_FAILURE;
  }
  printf("Success: data read from readBlock() matches data written\n");

  return EXIT_SUCCESS;
}

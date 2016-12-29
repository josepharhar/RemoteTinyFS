#include "test.h"

#include <string>

int main(int argc, char** argv) {
  char* token = argv[1];
  int return_code, disk;

  disk = openDisk(__FILE__, 0);
  if (disk < 0) {
    // disk already exists
  } else {
    // disk does not exist
  }

  return_code = initLibDisk(token);
  if (return_code) {
    FAIL("initLibDisk() returned " << return_code);
  }
  INFO("Successfully connected to server");
}

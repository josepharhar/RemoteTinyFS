#include "test.h"

int main(int argc, char** argv) {
  char* token = argv[1];
  int return_code;

  return_code = initLibDisk(token);
  if (return_code) {
    FAIL("initLibDisk() returned " << return_code);
  }
  INFO("Successfully connected to server");

}

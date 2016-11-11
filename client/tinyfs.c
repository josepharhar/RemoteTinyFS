#include <stdio.h>
#include <string.h>

#include "libDisk.h"

const char disk_name[] = "test_disk_name";
const int disk_bytes = 30;

const char buffer_data[] = "test buffer data";
const int buffer_size_bytes = 30;

int main(int argc, char** argv) {
  int disk = openDisk(disk_name, disk_bytes);

  void* write_buffer = calloc(buffer_size_bytes, sizeof(char));
  memcpy(write_buffer, buffer_data, strlen(buffer_data));
  writeBlock(disk, buffer_size_bytes, write_buffer);

  void* read_buffer = calloc(buffer_size_bytes, sizeof(char));
  readBlock(disk, buffer_size_bytes, read_buffer);

  printf("tinyfs read back string: \"%s\"\n", (char*) read_buffer);
}

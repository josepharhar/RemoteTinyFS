#ifndef TEST_H_
#define TEST_H_

#include "libDisk.h"

#include <stdlib.h>
#include <iostream>

#define FAIL(message)                                           \
  std::cerr << "[FAILURE " << __FILE__ << "] " << message << std::endl; \
  exit(EXIT_FAILURE);

#define INFO(message)                                           \
  std::cout << "[INFO " << __FILE__ << "] " << message << std::endl;

#endif  // TEST_H_

#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NOCOLOR='\033[0m'

echo -e "run-test.sh arg1: $1"
echo -e "run-test.sh arg2: $2"

if [ -z $1 ] || [ -z $2 ]; then
  echo -e "${RED}Usage: run-test.sh testName token${NOCOLOR}"
  exit 1
fi

echo -e "${BLUE}Compiling test $1${NOCOLOR}"
make $1
if [ $? != 0 ]; then
  echo -e "${RED}Test $1 failed to compile${NOCOLOR}"
  exit 1
fi

echo -e "${BLUE}Running test $1${NOCOLOR}"
./$1 $2 >> ${1}.out 2>&1
if [ $? != 0 ]; then
  echo -e "${RED}Test $1 failed, output in ${1}.out${NOCOLOR}"
  exit 1
else
  echo -e "${GREEN}Test $1 passed${NOCOLOR}"
  rm ${1}.out
  exit 0
fi

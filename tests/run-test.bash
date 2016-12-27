#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NOCOLOR='\033[0m'

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
#./$1 $2 >> ${1}.out 2>&1
./$1 $2 2>&1 | tee ${1}.out
#if [ $? != 0 ]; then
if [ ${PIPESTATUS[0]} != 0 ]; then
  echo -e "${RED}Test $1 failed, output in ${1}.out${NOCOLOR}"
  rm $1
  exit 1
else
  echo -e "${GREEN}Test $1 passed${NOCOLOR}"
  rm $1 ${1}.out
  exit 0
fi

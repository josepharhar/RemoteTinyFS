#!/bin/sh

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NOCOLOR='\033[0m'

if [ -z $1 ]; then
  echo "${RED}Usage: run-all-tests.sh token${NOCOLOR}"
  exit 1
fi

for testFile in *.c; do
  testName="${testFile%.*}"
  bash run-test.sh $testName $1
  #if [ $? != 0 ]; then
    # test failed. should we exit here?
  #fi
done

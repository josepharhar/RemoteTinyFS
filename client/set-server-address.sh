#!/bin/bash

echo "Enter Remote TinyFS Server address and press [ENTER]"
echo "Example: example.com:8080"
echo -n "> "
read ADDRESS

# Replace first line of makefile with LIBDISK_ADDRESS = "address"
sed -i "1s@.*@LIBDISK_ADDRESS = \\\\\"${ADDRESS}\\\\\"@" makefile

# Notify make that libDisk should be recompiled
touch libDisk.cc

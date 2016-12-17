#!/bin/bash

# stop if any command fails
set -e

# installation path
REMOTETINYFS_DIR=$HOME/remotetinyfs-bin$(getconf LONG_BIT)

# create install dir if it doesn't exist already
mkdir -p $REMOTETINYFS_DIR

# download protobuf source code
PROTOBUF_SRC_TAR=$REMOTETINYFS_DIR/protobuf-cpp-3.1.0.tar.gz
PROTOBUF_SRC_DIR=$REMOTETINYFS_DIR/protobuf-3.1.0
if [ ! -f $PROTOBUF_SRC_TAR ]; then
  # Google uses GitHub releases like this as their official download source; this link should be permanent.
  # See https://github.com/google/protobuf/releases for a list of releases
  wget "https://github.com/google/protobuf/releases/download/v3.1.0/protobuf-cpp-3.1.0.tar.gz" -O $PROTOBUF_SRC_TAR
fi
if [ ! -d $PROTOBUF_SRC_DIR ]; then
  tar -xzf $PROTOBUF_SRC_TAR -C $REMOTETINYFS_DIR
fi

# compile protobuf from source
cd $PROTOBUF_SRC_DIR
./autogen.sh
# ./configure required user input on the CSL because of a pax warning
./configure --prefix=$REMOTETINYFS_DIR
make -j6
make install

# make accessible for students
chmod -R 755 $REMOTETINYFS_DIR

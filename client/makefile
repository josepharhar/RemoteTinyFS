ARCH := $(shell getconf LONG_BIT)
REMOTETINYFS_DIR = $(HOME)/remotetinyfs-bin$(ARCH)

REMOTETINYFS_FLAGS = -L$(REMOTETINYFS_DIR) -L$(REMOTETINYFS_DIR)/lib -Wl,-rpath,$(REMOTETINYFS_DIR):$(REMOTETINYFS_DIR)/lib -lDisk

PKG_CONFIG_PATH = $(REMOTETINYFS_DIR)/lib/pkgconfig
PROTOBUF_CFLAGS := $(shell env PKG_CONFIG_PATH=$(PKG_CONFIG_PATH) pkg-config --cflags protobuf)
PROTOBUF_LIBS := $(shell env PKG_CONFIG_PATH=$(PKG_CONFIG_PATH) pkg-config --libs protobuf)

ifdef LIBDISK_DEBUG
CFLAGS = -Wall -DLIBDISK_DEBUG -g
else
CFLAGS = -Wall
endif

all: export

export: libDisk.so tfs_makefile
	cp libDisk.so $(REMOTETINYFS_DIR)
	chmod 755 $(REMOTETINYFS_DIR)/libDisk.so
	mkdir -p $(REMOTETINYFS_DIR)/studentfiles
	cp libDisk.h $(REMOTETINYFS_DIR)/studentfiles
	cp tfs_makefile $(REMOTETINYFS_DIR)/studentfiles
	sed -i '1s@.*@REMOTETINYFS_DIR = $(REMOTETINYFS_DIR)@' $(REMOTETINYFS_DIR)/studentfiles/tfs_makefile
	chmod -R 755 $(REMOTETINYFS_DIR)/studentfiles

libDisk.so: libDisk.o easywsclient.o model.pb.o
	gcc -shared -o $@ $^ $(PROTOBUF_LIBS)

libDisk.o: libDisk.cc libDisk.h model.pb.h
	g++ $(CFLAGS) $(PROTOBUF_CFLAGS) -fPIC -c $< -o $@

easywsclient.o: easywsclient.cpp easywsclient.hpp
	g++ $(CFLAGS) -fPIC -c $< -o $@

model.pb.o: model.pb.h
	g++ $(CFLAGS) $(PROTOBUF_CFLAGS) -fPIC -c model.pb.cc -o $@

model.pb.h: model.proto
	$(REMOTETINYFS_DIR)/bin/protoc --cpp_out=. model.proto

clean:
	rm -rf libDisk.so Disk.o easywsclient.o model.pb.o model.pb.h model.pb.cc

testDriver: testDriver.cc export
	g++ $< $(REMOTETINYFS_FLAGS) -o $@

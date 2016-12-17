LIBDISK_ADDRESS = \"home.arhar.net:4880\"

ARCH := $(shell getconf LONG_BIT)
REMOTETINYFS_DIR = $(HOME)/remotetinyfs-bin$(ARCH)

REMOTETINYFS_FLAGS = -L$(REMOTETINYFS_DIR) -L$(REMOTETINYFS_DIR)/lib -Wl,-rpath,$(REMOTETINYFS_DIR):$(REMOTETINYFS_DIR)/lib -lDisk

PKG_CONFIG_PATH = $(REMOTETINYFS_DIR)/lib/pkgconfig
PROTOBUF_CFLAGS := $(shell env PKG_CONFIG_PATH=$(PKG_CONFIG_PATH) pkg-config --cflags protobuf)
PROTOBUF_LIBS := $(shell env PKG_CONFIG_PATH=$(PKG_CONFIG_PATH) pkg-config --libs protobuf)

CFLAGS := -Wall -DLIBDISK_ADDRESS=$(LIBDISK_ADDRESS)
ifdef LIBDISK_DEBUG
CFLAGS += -g -DLIBDISK_DEBUG
endif

all: export

export: libDisk.so files-for-students
	cp libDisk.so $(REMOTETINYFS_DIR)
	chmod 755 $(REMOTETINYFS_DIR)/libDisk.so
	cp -r files-for-students $(REMOTETINYFS_DIR)
	cp libDisk.h $(REMOTETINYFS_DIR)/files-for-students
	sed -i '1s@.*@REMOTETINYFS_DIR = $(REMOTETINYFS_DIR)@' $(REMOTETINYFS_DIR)/files-for-students/makefile
	chmod -R 755 $(REMOTETINYFS_DIR)/files-for-students

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

testDriver: testDriver.cc libDisk.h export
	gcc $(CFLAGS) $< $(REMOTETINYFS_FLAGS) -o $@

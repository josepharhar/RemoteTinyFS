ARCH := $(shell getconf LONG_BIT)

PROTOBUF_EXISTS = $(shell pkg-config --exists protobuf ; echo $$?)
ifeq ($(PROTOBUF_EXISTS),0)
	PROTOBUF_INSTALL_PATH = $(shell pkg-config --variable=prefix protobuf)
else
	# These variables are used to set a pkg-config path for linking protobuf
	# libraries. If protobuf is already available on your system, then you may
	# ignore these. If you have compiled protobuf from source without root
	# access, then please change these paths to the path you used in the
	# ./configure script.
	# "$(HOME)/bin" is used here for "./configure --prefix=$HOME/bin"
	PROTOBUF_INSTALL_PATH_64 = $(HOME)/bin
	PROTOBUF_INSTALL_PATH_32 = $(HOME)/bin/i386
	PROTOBUF_INSTALL_PATH = $(PROTOBUF_INSTALL_PATH_$(ARCH))
endif

#PROTOBUF_STATIC_LIBRARY = $(PROTOBUF_INSTALL_PATH)/lib/libprotobuf-lite.a
PROTOBUF_STATIC_LIBRARY = $(PROTOBUF_INSTALL_PATH)/lib/libprotobuf.a
PKG_CONFIG_PATH = $(PROTOBUF_INSTALL_PATH)/lib/pkgconfig
PROTOBUF_CFLAGS = $(shell env PKG_CONFIG_PATH=$(PKG_CONFIG_PATH) pkg-config --cflags protobuf)
PROTOBUF_LIBS = $(shell env PKG_CONFIG_PATH=$(PKG_CONFIG_PATH) pkg-config --libs protobuf)

all: tinyfs

tinyfs: tinyfs.c libDisk.o easywsclient.o model.pb.o
	#gcc $(PROTOBUF_CFLAGS) tinyfs.c libDisk.o easywsclient.o model.pb.o $(PROTOBUF_LIBS) -lstdc++ -o tinyfs
	gcc $(PROTOBUF_CFLAGS) tinyfs.c libDisk.o easywsclient.o model.pb.o $(PROTOBUF_STATIC_LIBRARY) -lstdc++ -o tinyfs

#tinyfs: tinyfs.c libDisk.a
	#gcc tinyfs.c libDisk.a -o tinyfs

tinyfs.o: tinyfs.c libDisk.h
	gcc -c tinyfs.c

libDisk.a: libDisk.o easywsclient.o model.pb.o
	ar rvs libDisk.a libDisk.o easywsclient.o model.pb.o

libDisk.o: libDisk.cc libDisk.h model.pb.h
	g++ $(PROTOBUF_CFLAGS) -c libDisk.cc

client-dynamic: client.o easywsclient.o model.pb.o
	g++ client.o easywsclient.o model.pb.o -o client $(PROTOBUF_LIBS)

client-static: client.o easywsclient.o model.pb.o $(PROTOBUF_STATIC_LIBRARY)
	g++ client.o easywsclient.o model.pb.o $(PROTOBUF_STATIC_LIBRARY) -o client

client.o: client.cc easywsclient.hpp model.pb.h
	g++ $(PROTOBUF_CFLAGS) -c client.cc

easywsclient.o: easywsclient.cpp easywsclient.hpp
	g++ -c easywsclient.cpp

model.pb.o: model.pb.cc
	g++ $(PROTOBUF_CFLAGS) -c model.pb.cc

model.pb.cc: model.proto
	protoc --cpp_out=. model.proto

model.pb.h: model.proto
	protoc --cpp_out=. model.proto

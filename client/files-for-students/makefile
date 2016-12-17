REMOTETINYFS_DIR = <insert directory here>
REMOTETINYFS_FLAGS = -L$(REMOTETINYFS_DIR) -L$(REMOTETINYFS_DIR)/lib -Wl,-rpath,$(REMOTETINYFS_DIR):$(REMOTETINYFS_DIR)/lib -lDisk

CC = gcc
CFLAGS = -Wall -g
# Objects used for TinyFS Demo program
OBJS = tinyFsDemo.o libTinyFS.o
# Executable name prefix
PROG = tinyFsDemo
# Executable name and extra objects used for Remote TinyFS
PROG_REMOTE = $(PROG)-remote
OBJS_REMOTE = $(OBJS)
# Executable name and extra objects used for local file TinyFS
PROG_LOCAL = $(PROG)-local
OBJS_LOCAL = $(OBJS) libDisk.o

all: $(PROG_REMOTE)

$(PROG_LOCAL): $(OBJS_LOCAL)
	$(CC) $(CFLAGS) $^ -o $@

$(PROG_REMOTE): $(OBJS_REMOTE)
	$(CC) $(CFLAGS) $^ $(REMOTETINYFS_FLAGS) -o $@

tinyFsDemo.o: tinyFsDemo.c libTinyFS.h tinyFS.h tinyFsErrno.h
	$(CC) $(CFLAGS) -c $< -o $@

libTinyFS.o: libTinyFS.c libTinyFS.h tinyFS.h libDisk.h libDisk.o tinyFsErrno.h
	$(CC) $(CFLAGS) -c $< -o $@

libDisk.o: libDisk.c libDisk.h tinyFS.h tinyFsErrno.h
	$(CC) $(CFLAGS) -c $< -o $@

exampleDiskDriver: exampleDiskDriver.c libDisk.h
	$(CC) $(CFLAGS) $< $(REMOTETINYFS_FLAGS) -o $@

clean:
	rm -rf $(OBJS_LOCAL) $(OBJS_REMOTE) $(PROG_LOCAL) $(PROG_REMOTE)

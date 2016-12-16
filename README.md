# Remote TinyFS
## Fall 2016 CPE 400 Project by Joseph Arhar and Nicolas Avila
[Google Doc](https://docs.google.com/document/d/1Y0KHQ00_FKQ2uBxDKG3h4CV9MQzGDEgeOx28NvSPXnI)

# 1. Server Installation Instructions

# 2. Client Installation Instructions
These are instructions for setting up the remote disk library, presumably on the Cal Poly unix servers (unixXX.csc.calpoly.edu).
`cd` into the `client/` directory to run all commands in this section.
All Remote TinyFS client resources will be installed into either `/home/username/remotetinyfs-bin32` or `/home/username/remotetinyfs-bin64`, depending on the CPU architecture the setup is ran on. For example, running these steps on `unix3.csc.calpoly.edu` will install into `/home/username/remotetinyfs-bin32`, whereas `unix13.csc.calpoly.edu` will install into `/home/username/remotetinyfs-bin64`. In order to allow students to use Remote TinyFS on 32 or 64 bit servers, you should run these steps on both a 32 bit server and a 64 bit server.
## 2.1 Install Protobuf
Install protobuf via the bash script `install-protobuf.sh`. This create the directory `/home/username/remotetinyfs-binXX` and install the Protobuf library into it, which is required in order to compile and use the remote disk library.
### 2.1.1 Pax Warning on Cal Poly unix servers
While running `install-protobuf.sh` on the Cal Poly unix servers, a warning that looks like this may appear:
```
ATTENTION! pax archive volume change required.
Ready for archive volume: 1
Input archive name or "." to quit pax.
Archive name > 
```
If this warning appears, type `.` and press `[ENTER]` to continue the installation.
## 2.2 Setting Server Address
After successfully running `install-protobuf.sh`, run `set-server-address.sh` to tell the remote disk library which server to use. This must be the address of the server used in step #1.
`set-server-address.sh` modifies a line in `makefile`, which is used to hard-code the server address into the compiled disk library, `libDisk.so`.
## 2.3 Compiling `libDisk.so`
Run the command `make` to compile `libDisk.so` and copy it to `/home/username/remotetinyfs-binXX`.
This command will also copy the `files-for-students` directory to `/home/username/remotetinyfs-binXX`, to be used in step #3.
## Updating Server Address
If the server from step #1 is changed and the address used by the disk library is out of date, it can be updated be re-running steps 2.2 and 2.3.

# 3. Student Installation Instructions

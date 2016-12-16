# Remote TinyFS
## Fall 2016 CPE 400 Project by Joseph Arhar and Nicolas Avila
[Google Doc](https://docs.google.com/document/d/1Y0KHQ00_FKQ2uBxDKG3h4CV9MQzGDEgeOx28NvSPXnI)

# 1. Server Installation Instructions
These instructions were made for Amazon Linux but the server should build on most any Unix system. For those systems, use their package manage to get the packages below.
## 1.1 Install Git
Use git to pull down this repository. If your system does not have git:

    sudo yum install git

## 1.2 Install Java 8
The server uses Java 8 features and requires that you have Java 8 installed. To check which version of Java you are running,
    run:
    
    java -version

Java 8 is version 1.8.0 or better. If you do not have Java 8, you can use yum and alternatives to install it:
    
    sudo yum install java-1.8.0-openjdk-devel
    sudo /usr/sbin/alternatives --config java   # Select Java 8
    sudo /usr/sbin/alternatives --config javac  # Select Java 8

## 1.3 Install Maven
Maven is used to build and launch the server. If you're using yum, you will need to add the apache-maven repo. Just run these commands to install everything needed:

    sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
    sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
    sudo yum install -y apache-maven

## 1.4 Running The Server
To launch the server, from RemoteTinyFS/server, use:

    mvn spring-boot:run

This will start a process in your shell that is running the server. To get a list of registered credentials for that server instance, check server/logs/Credentials.log for the most recently printed list of user names and tokens.

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


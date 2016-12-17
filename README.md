# Remote TinyFS - Fall 2016 CPE 400 Project by Joseph Arhar and Nicolas Avila
[Google Doc](https://docs.google.com/document/d/1Y0KHQ00_FKQ2uBxDKG3h4CV9MQzGDEgeOx28NvSPXnI)

# 1. Server Installation Instructions
These instructions were made for Amazon Linux but the server should build on most any Unix system. For those systems, use their package manager to get the packages below.

## 1.1 Install Git
Use git to pull down this repository. If your system does not have git:
```
sudo yum install git
```

## 1.2 Install Java 8
The server uses Java 8 features and requires that you have Java 8 installed. To check which version of Java you are running, run:
```
java -version
```
Java 8 is version 1.8.0 or better. If you do not have Java 8, you can use yum and alternatives to install it:
```
sudo yum install java-1.8.0-openjdk-devel
sudo /usr/sbin/alternatives --config java   # Select Java 8
sudo /usr/sbin/alternatives --config javac  # Select Java 8
```

## 1.3 Install Maven
Maven is used to build and launch the server. If you're using yum, you will need to add the apache-maven repo. Just run these commands to install everything needed:
```
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
```

## 1.4 Clone this repository from GitHub
Run this command in your home directory:
```
git clone https://github.com/josepharhar/remotetinyfs
```
This will create a directory called `remotetinyfs`. All remaining steps will assume you are in the `remotetinyfs` directory.

## 1.5 Add usernames to `server/application.properties`
Open `server/application.properties` with your favorite text editor. It should look something like this:
```
remoteTinyFS.registeredUsers=avilan,jarhar,foaad
```
The comma-separated list has all of the account names which can connect to this server. Replace it with a comma separted list of usernames, one for each student. The value of these 'usernames' serves no functionality, as students will connect using only a 'password' created in a future step.

## 1.6 Running The Server
To launch the server, from inside `server/`, use:
```
mvn spring-boot:run
```
This will start a process in your shell that is running the server.
In order to run the server in the background instead, you can run
```
nohup mvn spring-boot:run > /dev/null 2>&1 &
disown
```
This is a quick and dirty solution to running the server in the background. There are much better solutions, such as using GNU screen. See a discussion here: http://stackoverflow.com/questions/4797050/how-to-run-process-as-background-and-never-die

## 1.7 Getting Student Credentials
After the server is started, open `server/logs/Credentials.log`. This contains a list of usernames mapped to their private token, which acts as a password for that user to connect to this server. Each private token should be sent to its corresponding student so they can connect to the server in step #3.

# 2. Client Installation Instructions
These are instructions for setting up the remote disk library, presumably on the Cal Poly unix servers (unixXX.csc.calpoly.edu).
`cd` into the `client/` directory to run all commands in this section.
All Remote TinyFS client resources will be installed into either `/home/username/remotetinyfs-bin32` or `/home/username/remotetinyfs-bin64`, depending on the CPU architecture the setup is ran on. For example, running these steps on `unix3.csc.calpoly.edu` will install into `/home/username/remotetinyfs-bin32`, whereas `unix13.csc.calpoly.edu` will install into `/home/username/remotetinyfs-bin64`. In order to allow students to use Remote TinyFS on 32 or 64 bit servers, you should run these steps on both a 32 bit server and a 64 bit server.

## 2.1 Clone this repository from GitHub
Run this command in your home directory:
```
git clone https://github.com/josepharhar/remotetinyfs
```
This will create a directory called `remotetinyfs`. All remaining steps will assume you are in the `remotetinyfs/client/` directory.

## 2.2 Install Protobuf
Install protobuf via the bash script `install-protobuf.sh`. This create the directory `/home/username/remotetinyfs-binXX` and install the Protobuf library into it, which is required in order to compile and use the remote disk library. The installation process will take several minutes while it compiles protobuf from source.

### 2.2.1 Pax Warning on Cal Poly unix servers
While running `install-protobuf.sh` on the Cal Poly unix servers, a warning that looks like this may appear:
```
ATTENTION! pax archive volume change required.
Ready for archive volume: 1
Input archive name or "." to quit pax.
Archive name > 
```
If this warning appears, type `.` and press enter to continue the installation.

## 2.3 Setting Server Address
After successfully running `install-protobuf.sh`, run `set-server-address.sh` to tell the remote disk library which server to use. This must be the address of the server used in step #1. It will prompt you for a URL. By default, the maven server will run on port 8080, so if your server has the ip `12.34.56.78`, then you would enter `12.34.56.78:8080` into `set-server-address.sh`.
`set-server-address.sh` modifies a line in `makefile`, which is used to hard-code the server address into the compiled disk library, `libDisk.so`.
If the server from step #1 is changed and the address used by the disk library is out of date, it can be updated be re-running this step and step #2.3.

## 2.4 Compiling `libDisk.so`
Run the command `make` to compile `libDisk.so` and copy it to `/home/username/remotetinyfs-binXX`.
This command will also copy the `files-for-students` directory to `/home/username/remotetinyfs-binXX`, to be used in step #3.

# 3. Student Installation Instructions

## 3.1 Copy `files-for-students` from instructor directory
The `files-for-students` directory, in `/home/username/remotetinyfs-binXX`, contains a makefile which builds the TinyFS project linked to the remote disk library built in step #2.
```
cp -r /home/username/remotetinyfs-binXX/files-for-students ~
```
The makefile in `files-for-students` allows students to build TinyFS with either their own `libDisk` implementation or use the remote disk library, by running either `make tinyFsDemo-local` or `make tinyFsDemo-remote`.

## 3.2 Use token from instructor
Your instructor should have sent you a private token which you will use as a password to connect to the server.
Your private token is used with `initLibDisk()` to connect and authenticate with the server.
Example:
```
initLibDisk("testToken39+A/C6M8Glvw==");
```
`exampleDiskDriver.c` can be used to test your token. Replace "YOUR_TOKEN_HERE" in `exampleDiskDriver.c` with your token, and run `make exampleDiskDriver` and `./exampleDiskDriver`.

## 3.3 Use the PolyFS Visualizer
To use the disk visualizer, go to the server's address in your web browser, including the port the server is running on. Example: `http://12.34.56.78:8080`.
Read the instructions for the PolyFS Visualizer by clicking the "Instructions" button on the webpage.
If you ran `exampleDiskDriver` in the last step, then a disk called `testDisk` should appear in the visualizer.

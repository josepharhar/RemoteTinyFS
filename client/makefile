all: client

client: client.cc easywsclient.o
	g++ client.cc easywsclient.o -o client

easywsclient.o: easywsclient.hpp easywsclient.cpp
	g++ -c easywsclient.cpp

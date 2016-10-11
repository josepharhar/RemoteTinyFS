FLAGS = -I beast/include -std=c++11
LINKFLAGS = -lboost_system -lpthread

all: client

client: client.cc
	g++ $(FLAGS) client.cc -o client $(LINKFLAGS)

#include <stdint.h>
#include "easywsclient.hpp"

#include <iostream>

int main(int argc, char** argv) {
  easywsclient::WebSocket::pointer websocket =
    easywsclient::WebSocket::from_url("ws://arhar.net:8080/foo");
  if (!websocket) {
    std::cout << "!websocket, exiting" << std::endl;
    return 1;
  }

  std::cout << "sending message" << std::endl;
  websocket->send("hello from c++ in the CSL!");
  std::cout << "sent message" << std::endl;

  while (websocket->getReadyState() != easywsclient::WebSocket::CLOSED) {
    websocket->poll();
  }

  delete websocket;
  return 0;
}

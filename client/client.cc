#include <stdint.h>
#include "easywsclient.hpp"

#include <iostream>

void HandleMessage(const std::string& message) {
  std::cout << "HandleMessage \"" << message << "\"" << std::endl;
}

int main(int argc, char** argv) {
  easywsclient::WebSocket::pointer websocket =
    easywsclient::WebSocket::from_url("ws://localhost:8080/register");
  if (!websocket) {
    std::cout << "!websocket, exiting" << std::endl;
    return 1;
  }

  char data[] = {10, 42, -56, -68, 50, 35, 82, -17, -65, -67, -17, -65, -67, 13, -17, -65, -67, 104, -17, -65, -67, 103, 14, 106, -17, -65, -67, -17, -65, -67, 47, 113, 50, -17, -65, -67, -17, -65, -67, 53, -17, -65, -67, 35, 18, 4, 100, 117, 109, 98, 0};

  std::cout << "sending message" << std::endl;
  websocket->send(data);
  std::cout << "sent message" << std::endl;

  while (websocket->getReadyState() != easywsclient::WebSocket::CLOSED) {
    websocket->poll();
    websocket->dispatch(HandleMessage);
  }

  delete websocket;
  return 0;
}

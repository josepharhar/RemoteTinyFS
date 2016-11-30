package com.tinyfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

import com.tinyfs.handler.ClientHandler;

@Configuration
public class ExceptionConfig {
  @Bean
  public ExceptionWebSocketHandlerDecorator exceptionWebSocketHandlerDecorator(
      final ClientHandler clientHandler) {
    return new ExceptionWebSocketHandlerDecorator(clientHandler);
  }
}

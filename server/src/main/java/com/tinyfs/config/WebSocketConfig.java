package com.tinyfs.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.tinyfs.handler.ClientHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void registerWebSocketHandlers(
      final WebSocketHandlerRegistry registry) {
    registry
      .addHandler(applicationContext.getBean(ClientHandler.class), "/client")
      .setAllowedOrigins("*");
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}

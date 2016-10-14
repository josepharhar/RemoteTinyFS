package com.tinyfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.handler.ClientHandler;
import com.tinyfs.handler.RegistrationHandler;
import com.tinyfs.handler.WriteHandler;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

@Configuration
public class HandlerConfig {

  @Bean
  public ClientHandler clientHandler(
      final ClientRegistrationRequestValidator registrationRequestValidator,
      final RegistrationHandler registrationHandler,
      final WriteHandler writeHandler) {
    return new ClientHandler(
      registrationRequestValidator,
      registrationHandler,
      writeHandler);
  }

  @Bean
  public RegistrationHandler registrationHandler(
      final ClientCacheAdapter clientCacheAdapter) {
    return new RegistrationHandler(
      clientCacheAdapter);
  }

  @Bean
  public WriteHandler writeHandler(
      final ClientCacheAdapter clientCacheAdapter) {
    return new WriteHandler(
        clientCacheAdapter);
  }
}

package com.tinyfs.config;

import org.springframework.context.annotation.Bean;

import com.tinyfs.credentials.CredentialsObfuscator;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

public class ValidationConfig {
  @Bean
  public ClientRegistrationRequestValidator registrationRequestValidator(
      final CredentialsObfuscator obfuscator) {
    return new ClientRegistrationRequestValidator(obfuscator);
  }
}

package com.tinyfs.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyfs.credentials.ClientCredentialsProvider;
import com.tinyfs.credentials.CredentialsObfuscator;
import com.tinyfs.exception.CredentialsException;

@Configuration
public class CredentialsConfig {

  /**
   * This key is in no way secure, and merely to serve as a proof of concept.
   * This project will use a much more secure scheme, where a key is not statically used from code.
   *
   * @TODO Pull a key from a local file in the repository, not importing it statically. RemoteTinyFS/issues/2
   */
  private static final String DES_KEY_RAW = "a9/Vbr+oosQ=";
  private static final byte[] DES_KEY_DECODED = Base64.getDecoder().decode(DES_KEY_RAW);

  @Bean
  SecretKey secretKey() {
    return new SecretKeySpec(DES_KEY_DECODED, 0, DES_KEY_DECODED.length, "DES");
  }

  @Bean
  CredentialsObfuscator obfuscator(final SecretKey secretKey)
      throws CredentialsException {
    return new CredentialsObfuscator(secretKey);
  }

  @Bean
  ClientCredentialsProvider clientCredentialsProvider() {
    return new ClientCredentialsProvider();
  }
}
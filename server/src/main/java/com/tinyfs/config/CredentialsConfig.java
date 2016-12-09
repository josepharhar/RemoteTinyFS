package com.tinyfs.config;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Throwables;
import com.tinyfs.exception.CredentialsException;

@Configuration
public class CredentialsConfig {

  public static final String USERNAME_LIST_KEY = "usernames";
  public static final String CREDENTIALS_ENCRYPTION_SCHEME = "AES";

  private static final String CREDENTIALS_KEY_NAME = "credentials.key";
  private static final int AES_KEY_LENGTH = 16;

  @Bean
  public SecretKey secretKey() throws CredentialsException {
    byte[] bytes = Arrays.copyOf(
      Base64.getDecoder().decode(getKeyText(CREDENTIALS_KEY_NAME)),
      AES_KEY_LENGTH);
    return new SecretKeySpec(
      bytes,
      0,
      AES_KEY_LENGTH,
      CREDENTIALS_ENCRYPTION_SCHEME);
  }

  private String getKeyText(final String resourceName) throws CredentialsException {
    File file = new File(CredentialsConfig.class.getClassLoader().getResource("credentials.key").getFile());

    try (Scanner in = new Scanner(file)) {
      return in.nextLine();
    } catch (Exception e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    throw new CredentialsException("No valid key found.");
  }
}
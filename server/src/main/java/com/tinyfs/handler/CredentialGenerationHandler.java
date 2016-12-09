package com.tinyfs.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.credentials.CredentialsObfuscator;

@Component
public class CredentialGenerationHandler {

  private static final Logger LOGGER = LogManager.getLogger(CredentialGenerationHandler.class);

  private static final Level CREDENTIALS_LEVEL = Level.forName("CREDENTIALS", 250);

  private final CredentialsObfuscator credentialsObfuscator;
  private final List<String> allowableUsers;

  @Inject
  public CredentialGenerationHandler(
      final CredentialsObfuscator credentialsObfuscator,
      @Value("#{'${remoteTinyFS.registeredUsers}'.split(',')}")
      final List<String> allowableUsers) {
    this.credentialsObfuscator = credentialsObfuscator;
    this.allowableUsers = allowableUsers;
  }

  @PostConstruct
  public void generateCredentials() {
    LOGGER.log(
        CREDENTIALS_LEVEL,
        "Credentials generated for this instance:");
    for (String username : allowableUsers) {
      String token = new String(credentialsObfuscator.obfuscateClientCredentials(toClientCredentials(username)));

      LOGGER.log(
        CREDENTIALS_LEVEL,
        String.format("\t%s\t\t %s", username, token));
    }
  }

  private ClientCredentials toClientCredentials(final String username) {
    return ClientCredentials.newBuilder()
      .setUsername(username)
      .build();
  }
}

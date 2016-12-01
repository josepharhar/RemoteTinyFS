package com.tinyfs.handler;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.tinyfs.auth.ClientCredentialsProto.AccessCredentials;
import com.tinyfs.credentials.CredentialsObfuscator;
import com.tinyfs.credentials.user.AllowableUsers;

@Component
public class CredentialGenerationHandler {
  private static final Logger LOGGER = LogManager.getLogger(CredentialGenerationHandler.class);

  private static final Level CREDENTIALS_LEVEL = Level.forName("CREDENTIALS", 250);

  private final CredentialsObfuscator credentialsObfuscator;


  @Inject
  public CredentialGenerationHandler(final CredentialsObfuscator credentialsObfuscator) {
    this.credentialsObfuscator = credentialsObfuscator;
  }

  @PostConstruct
  public void generateCredentials() {
    for (String username : AllowableUsers.ALLOWABLE_USERS) {
      String token = new String(credentialsObfuscator.obfuscateAccessCredentials(toAccessCredentials(username)));

      LOGGER.log(
        CREDENTIALS_LEVEL,
        String.format("%s :: %s", username, token));
    }
  }

  private AccessCredentials toAccessCredentials(final String username) {
    return AccessCredentials.newBuilder()
      .setName(username)
      .build();
  }
}

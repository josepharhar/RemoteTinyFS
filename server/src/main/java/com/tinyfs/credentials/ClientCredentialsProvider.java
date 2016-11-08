package com.tinyfs.credentials;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;

// TODO: Make this class thread safe, find a better way to store known client credentials.
@Component
public class ClientCredentialsProvider {
  private Set<ClientCredentials> knownClientCredentials;

  public ClientCredentialsProvider() {
    this.knownClientCredentials = new HashSet<ClientCredentials>();
  }

  public ClientCredentials provideNewClientCredentials() {
    ClientCredentials clientCredentials =
      ClientCredentials.newBuilder()
        .setClientId(UUID.randomUUID().toString())
        .build();

    knownClientCredentials.add(clientCredentials);

    return clientCredentials;
  }

  public void unregisterClient(final ClientCredentials clientCredentials) {
    knownClientCredentials.remove(clientCredentials);
  }

  public boolean isKnownClient(final ClientCredentials clientCredentials) {
    return knownClientCredentials.contains(clientCredentials);
  }
}

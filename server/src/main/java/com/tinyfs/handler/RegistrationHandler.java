package com.tinyfs.handler;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.model.ServiceModel.ClientRegistrationResponse;

@Component
public class RegistrationHandler {

  private final ClientCacheAdapter clientCacheAdapter;

  @Inject
  public RegistrationHandler(
      final ClientCacheAdapter clientCacheAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
  }

  public BinaryMessage registerClient(
      final String username) {
    String sessionId = UUID.randomUUID().toString();

    clientCacheAdapter.registerClient(sessionId, username);

    ClientRegistrationResponse response =
      ClientRegistrationResponse.newBuilder()
        .setResponseCode(ClientRegistrationResponse.ResponseCode.SUCCESS)
        .setSessionId(sessionId)
        .build();

    return new BinaryMessage(response.toByteArray());
  }

}

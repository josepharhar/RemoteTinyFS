package com.tinyfs.validation;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.credentials.CredentialsObfuscator;
import com.tinyfs.model.ServiceModel.ClientRegistrationRequest;

@Component
public class ClientRegistrationRequestValidator {

  private final CredentialsObfuscator credentialsObfuscator;

  @Inject
  public ClientRegistrationRequestValidator(final CredentialsObfuscator credentialsObfuscator) {
    this.credentialsObfuscator = credentialsObfuscator;
  }

  public Optional<ClientRegistrationRequest> toClientRegistrationRequest(final byte[] payload) {
    Optional<ClientRegistrationRequest> request = Optional.empty();

    try {
      request = Optional.of(ClientRegistrationRequest.parseFrom(payload));
    } catch (InvalidProtocolBufferException exception) {
      exception.printStackTrace();
      return Optional.empty();
    }

    return request;
  }

  public ClientCredentials toClientCredentials(final byte[] token) {
    return credentialsObfuscator.deobfuscateClientCredentials(token);
  }
}
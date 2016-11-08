package com.tinyfs.handler;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.exception.InvalidArgumentException;
import com.tinyfs.model.ServiceModel.ClientRegistrationRequest;
import com.tinyfs.model.ServiceModel.ClientRequest;
import com.tinyfs.model.ServiceModel.WriteRequest;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

@Component
public class ClientHandler extends TextWebSocketHandler {

  private final ClientRegistrationRequestValidator registrationRequestValidator;
  private final RegistrationHandler registrationHandler;
  private final WriteHandler writeHandler;

  @Inject
  public ClientHandler(
      final ClientRegistrationRequestValidator registrationRequestValidator,
      final RegistrationHandler registrationHandler,
      final WriteHandler writeHandler) {
    this.registrationRequestValidator = registrationRequestValidator;
    this.registrationHandler = registrationHandler;
    this.writeHandler = writeHandler;
  }

  @Override
  public void handleTextMessage(
      final WebSocketSession session,
      final TextMessage message)
          throws Exception {
    ClientRequest request =
      toClientRequest(message.getPayload().getBytes())
        .orElseThrow(() -> new InvalidArgumentException());

    Any operationParameters = request.getRequest();
    if (operationParameters.is(ClientRegistrationRequest.class)) {
      ClientRegistrationRequest registrationRequest =
        operationParameters.unpack(ClientRegistrationRequest.class);
      ClientCredentials clientCredentials =
        registrationRequestValidator.toClientCredentials(registrationRequest.getToken().toByteArray());

      session.sendMessage(
        registrationHandler.registerClient(
          clientCredentials,
          registrationRequest.getFsNamesList()));
    } else if (operationParameters.is(WriteRequest.class)) {
      WriteRequest writeRequest =
        operationParameters.unpack(WriteRequest.class);

      writeHandler.performWriteRequest(
        writeRequest.getSessionId(),
        writeRequest.getFileSystem(),
        writeRequest.getMessage().toString());
    } else {
      throw new InvalidArgumentException("Unrecognized operation.");
    }
  }

  private Optional<ClientRequest> toClientRequest(final byte[] payload) {
    try {
      return Optional.of(ClientRequest.parseFrom(payload));
    } catch (InvalidProtocolBufferException e) {
      return Optional.empty();
    }
  }
}

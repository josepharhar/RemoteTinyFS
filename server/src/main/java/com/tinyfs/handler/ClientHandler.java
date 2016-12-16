package com.tinyfs.handler;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.google.common.base.Throwables;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.exception.InvalidArgumentException;
import com.tinyfs.exception.InvalidCredentialsException;
import com.tinyfs.model.ServiceModel.ClientRegistrationRequest;
import com.tinyfs.model.ServiceModel.ClientRegistrationResponse;
import com.tinyfs.model.ServiceModel.ClientRequest;
import com.tinyfs.model.ServiceModel.ReadRequest;
import com.tinyfs.model.ServiceModel.ReadResponse;
import com.tinyfs.model.ServiceModel.WriteRequest;
import com.tinyfs.model.ServiceModel.WriteResponse;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

@Component
public class ClientHandler extends BinaryWebSocketHandler {

  private final ClientRegistrationRequestValidator registrationRequestValidator;
  private final RegistrationHandler registrationHandler;
  private final WriteHandler writeHandler;
  private final ReadHandler readHandler;

  @Inject
  public ClientHandler(
      final ClientRegistrationRequestValidator registrationRequestValidator,
      final RegistrationHandler registrationHandler,
      final WriteHandler writeHandler,
      final ReadHandler readHandler) {
    this.registrationRequestValidator = registrationRequestValidator;
    this.registrationHandler = registrationHandler;
    this.writeHandler = writeHandler;
    this.readHandler = readHandler;
  }

  /**
   * Wraps the dispatch to catch and print exceptions.
   */
  @Override
  public void handleBinaryMessage(
      WebSocketSession session,
      BinaryMessage message)
          throws Exception {
    try {
      dispatchBinaryMessage(session, message);
    } catch (Exception e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }
  }

  public void dispatchBinaryMessage(
      final WebSocketSession session,
      final BinaryMessage message)
          throws Exception {
    ClientRequest request;
    try {
      request = ClientRequest.parseFrom(message.getPayload().array());
    } catch (InvalidProtocolBufferException e) {
      throw new Exception("Undecipherable request", e);
    }

    Any operationParameters = request.getRequest();
    if (operationParameters.is(ClientRegistrationRequest.class)) {
      ClientRegistrationRequest registrationRequest =
        operationParameters.unpack(ClientRegistrationRequest.class);

      try {
        ClientCredentials clientCredentials = registrationRequestValidator
          .toClientCredentials(registrationRequest.getToken().toByteArray());
        session.sendMessage(
          registrationHandler.registerClient(clientCredentials.getUsername()));
      } catch (InvalidCredentialsException e) {
        session.sendMessage(new BinaryMessage(
            ClientRegistrationResponse.newBuilder()
              .setSessionId("")
              .setResponseCode(ClientRegistrationResponse.ResponseCode.BAD_TOKEN)
              .build()
              .toByteArray()));
      }
    } else if (operationParameters.is(WriteRequest.class)) {
      WriteRequest writeRequest =
        operationParameters.unpack(WriteRequest.class);

      writeHandler.performWriteRequest(
        writeRequest.getSessionId(),
        writeRequest.getFile(),
        writeRequest.getMessage().toByteArray(),
        writeRequest.getOffset());

      session.sendMessage(new BinaryMessage(
          WriteResponse.newBuilder()
            .setResponseCode(WriteResponse.ResponseCode.SUCCESS)
            .build()
            .toByteArray()));
    } else if (operationParameters.is(ReadRequest.class)) {
      ReadRequest readRequest =
        operationParameters.unpack(ReadRequest.class);

      session.sendMessage(
          new BinaryMessage(
            ReadResponse.newBuilder()
              .setResponseCode(ReadResponse.ResponseCode.SUCCESS)
              .setMessage(ByteString.copyFrom(
                readHandler.performReadRequest(
                  readRequest.getSessionId(),
                  readRequest.getFile(),
                  readRequest.getOffset(),
                  readRequest.getSize())))
              .build()
              .toByteArray()));
    } else {
      throw new InvalidArgumentException("Unrecognized operation.");
    }
  }
}

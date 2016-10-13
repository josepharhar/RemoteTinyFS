package foo;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class FooHandler extends TextWebSocketHandler {

  private WebSocketSession session;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    System.out.println("FooHandler::afterConnectionEstablished");
    this.session = session;
  }

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) {
    System.out.println("FooHandler::handleTextMessage \"" + message.getPayload() + "\"");
    System.out.println("this.session == session: " + (this.session == session));
    try {
      session.sendMessage(new TextMessage("Hello from Java Spring server!"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

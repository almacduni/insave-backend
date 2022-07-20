package org.save.websocket;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.model.dto.in10.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Log4j2
public class WebSocketController {

  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/tickers")
  @SendTo("/topic/events")
  public MessageDto sendMessage(@Payload MessageDto messageDto, Principal user) {
    log.info("Principal username {}", user.getName());
    log.info("Message from websocket {}", messageDto);
    messagingTemplate.convertAndSendToUser(
        user.getName(), "/queue/tickers", new MessageDto("Private WebSocket Message"));
    return new MessageDto("Public WebSocket Message");
  }
}

package org.save.websocket;

import lombok.RequiredArgsConstructor;
import org.save.model.dto.in10.MessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

  private final SimpMessagingTemplate messagingTemplate;

  @Scheduled(fixedRate = 1000)
  public void sendMessage() {
    MessageDto messageDto = new MessageDto();
    messageDto.setMessage("AAPL, TSLA");
    messagingTemplate.convertAndSendToUser("Gs5Jmvn", "/queue/tickers", messageDto);
  }
}

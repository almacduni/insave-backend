package org.save.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSender {

  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String username;

  public void send(String emailTo, String subject, String message) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(username);
    simpleMailMessage.setTo(emailTo);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setText(message);

    javaMailSender.send(simpleMailMessage);
  }
}

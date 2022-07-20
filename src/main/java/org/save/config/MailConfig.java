package org.save.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.port}")
  private int port;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.protocol}")
  private String protocol;

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(host);
    javaMailSender.setUsername(username);
    javaMailSender.setPort(port);
    javaMailSender.setPassword(password);

    Properties javaMailProperties = javaMailSender.getJavaMailProperties();
    javaMailProperties.setProperty("mail.transport.protocol", protocol);

    return javaMailSender;
  }
}

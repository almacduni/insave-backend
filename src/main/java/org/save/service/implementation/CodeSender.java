package org.save.service.implementation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeSender {

  private final MailSender mailSender;
  private final Map<String, Integer> storedCodes = new HashMap<>();

  public void send(String key, String email, String emailTitle) {
    final int activationCode = 100000 + ThreadLocalRandom.current().nextInt(900000);

    // TODO: move to Redis
    storedCodes.put(key, activationCode);
    mailSender.send(email, emailTitle, String.valueOf(activationCode));

    log.info("Sent activation code={}, key={}, email={}", storedCodes, key, email);
  }

  public boolean codeIsValid(String key, int activationCode) {
    log.info("key={} code={}", key, activationCode);
    Integer storedCode =
        Optional.ofNullable(storedCodes.get(key))
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activation code"));

    return storedCode.equals(activationCode);
  }

  public void remove(String key) {
    storedCodes.remove(key);
  }
}

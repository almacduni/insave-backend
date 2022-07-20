package org.save.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UnirestConfig {

  private final ObjectMapper objectMapper;

  @PostConstruct
  public void postConstruct() {
    setObjectMapper();
  }

  private void setObjectMapper() {
    Unirest.setObjectMapper(
        new com.mashape.unirest.http.ObjectMapper() {
          @Override
          public <T> T readValue(String s, Class<T> aClass) {
            try {
              return objectMapper.readValue(s, aClass);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }

          @Override
          public String writeValue(Object o) {
            try {
              return objectMapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
          }
        });
  }
}

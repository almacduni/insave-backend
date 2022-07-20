package org.save.client;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.model.dto.social.gif.GifDescription;
import org.save.model.dto.social.gif.GifResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Log4j2
public class TenorGifClient {

  @Value("${insave.integration.media.gif.api_key}")
  private final String API_KEY;

  @Value("${insave.integration.media.gif.response.limit}")
  private final int GIF_LIMIT;

  private final RestTemplate restTemplate;

  public List<String> searchGifsByName(String searchTerm) {
    String url = "https://g.tenor.com/v1/search?q={searchTerm}&key={API_KEY}&limit={GIF_LIMIT}";
    return searchGifs(url, searchTerm, API_KEY, GIF_LIMIT);
  }

  public List<String> getTendingGifs() {
    String url = "https://g.tenor.com/v1/trending?key={API_KEY}&limit={GIF_LIMIT}";
    return searchGifs(url, API_KEY, GIF_LIMIT);
  }

  @SuppressWarnings("all")
  private List<String> searchGifs(String url, Object... uriVariables) {
    GifResponse gifResponse =
        restTemplate.getForEntity(url, GifResponse.class, uriVariables).getBody();
    List<GifDescription> gifDescriptions = gifResponse.getResults();

    return gifDescriptions.stream().map(GifDescription::getItemurl).collect(Collectors.toList());
  }
}

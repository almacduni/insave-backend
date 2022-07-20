package org.save.util.mapper;

import org.save.model.dto.search.SearchTickerResponse;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.stereotype.Component;

@Component
public class TickerMapper {

  public SearchTickerResponse convertToSearchTickerResponse(Ticker ticker) {
    return new SearchTickerResponse(ticker.getName(), ticker.getCompany());
  }
}

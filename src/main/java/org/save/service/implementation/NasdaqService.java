package org.save.service.implementation;

import lombok.RequiredArgsConstructor;
import org.save.model.dto.nasdaq.QuarterlyEarningsResponse;
import org.save.util.parsers.NasdaqParser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NasdaqService {
  private final NasdaqParser nasdaqParser;

  public QuarterlyEarningsResponse getQuarterlyEarnings(String ticker) {
    ticker = ticker.toLowerCase();
    String url = "https://www.nasdaq.com/market-activity/stocks/" + ticker + "/earnings";

    return nasdaqParser.getQuarterlyEarnings(url);
  }
}

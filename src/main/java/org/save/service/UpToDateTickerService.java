package org.save.service;

import java.math.RoundingMode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.save.client.FinancialModellingClient;
import org.save.model.dto.search.GeneralInfoInSearchDto;
import org.save.model.dto.ticker.UpToDateTickerResponse;
import org.springframework.stereotype.Service;

/** Service for managing {@link UpToDateTickerResponse} */
@Service
@Log4j2
@RequiredArgsConstructor
public class UpToDateTickerService {

  private final FinancialModellingClient financialModellingClient;

  /**
   * Gets optional {@link UpToDateTickerResponse} by provided ticker
   *
   * @param ticker ticker
   * @return optional {@link UpToDateTickerResponse}
   */
  public Optional<UpToDateTickerResponse> getUpToDateTicker(String ticker) {
    GeneralInfoInSearchDto esp = financialModellingClient.getCompanyInfo(ticker);
    if (esp == null) {
      return Optional.empty();
    } else {
      UpToDateTickerResponse upToDateTicker =
          new UpToDateTickerResponse(
              esp.getPrice().setScale(2, RoundingMode.DOWN),
              esp.getChange().setScale(2, RoundingMode.DOWN),
              esp.getChangesPercentage().setScale(2, RoundingMode.DOWN));
      return Optional.of(upToDateTicker);
    }
  }
}

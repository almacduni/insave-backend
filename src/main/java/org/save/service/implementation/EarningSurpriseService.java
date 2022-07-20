package org.save.service.implementation;

import java.util.List;
import org.save.model.dto.financialmodelling.EarningSurpriseDto;
import org.save.util.TickerByCompanyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EarningSurpriseService {

  @Autowired private TickerByCompanyName tickerByCompanyName;

  public List<EarningSurpriseDto> getEarningSurprise(String ticker) {
    return tickerByCompanyName.getEarningSurprise(ticker);
  }
}

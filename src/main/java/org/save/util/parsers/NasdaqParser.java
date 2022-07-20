package org.save.util.parsers;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.save.model.dto.nasdaq.Forecast;
import org.save.model.dto.nasdaq.QuarterlyEarningsResponse;
import org.save.model.dto.nasdaq.SurpriseAmount;
import org.springframework.stereotype.Component;

@Component
public class NasdaqParser {
  public WebDriver initializeDriver() {
    System.setProperty("webdriver.chrome.driver", "driver/chromedriver");

    return new ChromeDriver();
  }

  public Document getPage(String url) {
    WebDriver webDriver = initializeDriver();

    try {
      webDriver.get(url);
    } catch (Exception ignored) {
    }

    Document page = Jsoup.parse(webDriver.getPageSource());
    webDriver.close();

    return page;
  }

  public QuarterlyEarningsResponse getQuarterlyEarnings(String url) {
    Document currentPage = getPage(url);

    if (currentPage == null) {
      return null;
    }

    List<SurpriseAmount> surpriseAmountList = new ArrayList<>();
    List<Forecast> forecastList = new ArrayList<>();

    Elements earningsSurpriseAmountTable =
        currentPage.select("tbody[class=earnings-surprise__table-body]");

    Element earningsForecast =
        currentPage
            .select("div[class=earnings-forecast__section earnings-forecast__section--quarterly]")
            .first();
    Element earningsForecastTable =
        earningsForecast.select("tbody[class=earnings-forecast__table-body]").first();

    for (int i = 0; i < 4; i++) {
      Element earningsSurpriseAmountElement =
          earningsSurpriseAmountTable
              .select("tr[class=earnings-surprise__row earnings-surprise__row--body]")
              .get(i);

      SurpriseAmount surpriseAmount =
          new SurpriseAmount(
              earningsSurpriseAmountElement
                  .select("th[class=earnings-surprise__table-cell]")
                  .text(),
              earningsSurpriseAmountElement
                  .select("td[class=earnings-surprise__table-cell]")
                  .get(0)
                  .text(),
              earningsSurpriseAmountElement
                  .select("td[class=earnings-surprise__table-cell]")
                  .get(1)
                  .text(),
              earningsSurpriseAmountElement
                  .select("td[class=earnings-surprise__table-cell]")
                  .get(2)
                  .text());

      surpriseAmountList.add(surpriseAmount);
    }

    for (int i = 0; i < 2; i++) {
      Element earningsForecastElement =
          earningsForecastTable
              .select("tr[class=earnings-forecast__row earnings-forecast__row--body]")
              .get(i);

      Forecast forecast =
          new Forecast(
              earningsForecastElement.select("th[class=earnings-forecast__cell]").text(),
              earningsForecastElement.select("td[class=earnings-forecast__cell]").first().text());

      forecastList.add(forecast);
    }

    return new QuarterlyEarningsResponse(surpriseAmountList, forecastList);
  }
}

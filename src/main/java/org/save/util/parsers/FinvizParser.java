package org.save.util.parsers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.save.model.dto.financialmodelling.CompanyDescription;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class FinvizParser {

  private final int TICKER_LIMIT = 8080;

  public Set<Ticker> getTickers() {

    log.info("[START] Parsing tickers from finviz.com ...");
    final long startTime = System.currentTimeMillis();

    final Set<Ticker> tickerSet = new HashSet<>(TICKER_LIMIT);

    final String URI_TEMPLATE = "https://finviz.com/screener.ashx?v=152&c=0,1,2,6,62&r={rValue}";
    int rValue = 0;

    try {

      while (rValue <= TICKER_LIMIT) {
        collectTickersFromDocument(
            Jsoup.parse(new URL(URI_TEMPLATE.replace("{rValue}", String.valueOf(rValue))), 300000),
            tickerSet);

        rValue += 20;

        if (rValue % 400 == 0) {
          log.info(
              "[PROCESS] Parsing tickers from finviz.com, tickerSetSize={} rValue={}",
              tickerSet.size(),
              rValue);
        }
      }

      log.info(
          "[FINISH] Parsing tickers from finviz.com, tickerSetSize={} time={}ms",
          tickerSet.size(),
          (System.currentTimeMillis() - startTime));

    } catch (Exception exception) {
      log.error("Ticker parsing exception:", exception);
    }

    return tickerSet;
  }

  private void collectTickersFromDocument(Document document, Set<Ticker> tickerSet) {

    Elements tableTrElements =
        document.select("div[id=\"screener-content\"]").select("tr[valign=\"top\"]");

    tableTrElements.forEach(
        trElement -> {
          Elements tdElements = trElement.select("td");
          Ticker ticker = new Ticker();

          ticker.setId(Long.valueOf(tdElements.get(0).text()));
          ticker.setName(tdElements.get(1).text());
          ticker.setCompany(tdElements.get(2).text());
          ticker.setMarketCapitalization(tdElements.get(3).text());
          try {
            ticker.setAnalystRecommendation(Double.parseDouble(tdElements.get(4).text()));

            if (!tdElements.select("span[class=\"is-red\"]").isEmpty()) {
              ticker.setAnalystRecommendation(-1 * ticker.getAnalystRecommendation());
            }
          } catch (NullPointerException | NumberFormatException exception) {
            ticker.setAnalystRecommendation(0D);
          }

          tickerSet.add(ticker);
        });
  }

  public static Document getPage(String ticker) {
    String url = "https://finviz.com/quote.ashx?t=" + ticker;
    Document page = null;
    try {
      page = Jsoup.parse(new URL(url), 3000);
      return page;
    } catch (Exception ignored) {
      log.error("FINVIZ ERROR");
      return null;
    }
  }

  public CompanyDescription getCompanyDescription(String ticker) {
    List<String> list = new ArrayList<>();

    Document page = getPage(ticker);
    if (page == null) {
      return new CompanyDescription(0.0, 0.0, 0.0, 0.0);
    }
    Elements pageElement =
        page.select("table[class=snapshot-table2]")
            .select("tr[class=table-dark-row]")
            .select("td[class=snapshot-td2]");
    for (Element element : pageElement) {

      if (element.text().equals("-")) {
        list.add("0.0");
      } else {
        list.add(element.text());
      }
    }
    if (list.size() == 0) {
      return new CompanyDescription(0.0, 0.0, 0.0, 0.0);
    }
    return new CompanyDescription(
        Double.parseDouble(list.get(3).replace("%", "")),
        Double.parseDouble(list.get(15).replace("%", "")),
        Double.parseDouble(list.get(28)),
        Double.parseDouble(list.get(66)));
  }
}

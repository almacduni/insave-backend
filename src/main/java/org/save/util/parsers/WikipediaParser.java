package org.save.util.parsers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.save.exception.NoSuchObjectException;
import org.save.exception.WikipediaParserException;
import org.save.repo.TickerRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class WikipediaParser {

  private final TickerRepository tickerRepository;

  // return the first sentence from the company description text from Wikipedia.
  private static final String regexForSelectOneFirstSentenceFromCompanyDescription =
      "^.+?\\.\\s[A-ZА-Я]";
  // returns the first and second sentences from the company description text from Wikipedia.
  private static final String regexForSelectTwoFirstSentencesFromCompanyDescription =
      "^.+?\\.\\s[А-ЯA-Z].+?\\.\\s[А-ЯA-Z]";

  public String getDescriptionData(String tickerName) {
    log.info("[START] Parsing tickers from wikipedia.org ...");
    final long startTime = System.currentTimeMillis();
    String description = null;
    try {
      String companyName =
          tickerRepository
              .findTickerByName(tickerName)
              .orElseThrow(() -> new NoSuchObjectException("Ticker " + tickerName + " not found"))
              .getCompany();
      if (!tickerName.equals("AAPL")) {
        companyName = companyName.replaceAll(",", "").trim();
      }
      String url = "https://en.wikipedia.org/w/index.php?search=" + companyName;
      description = Jsoup.connect(url).get().select("div[class=mw-parser-output]>p").text();
      Pattern firstSentencePattern =
          Pattern.compile(regexForSelectOneFirstSentenceFromCompanyDescription);
      Matcher firstSentence = firstSentencePattern.matcher(description);
      Pattern twoSentencesPattern =
          Pattern.compile(regexForSelectTwoFirstSentencesFromCompanyDescription);
      Matcher twoSentences = twoSentencesPattern.matcher(description);

      if (description.length() < 100) {
        while (twoSentences.find()) {
          description = description.substring(twoSentences.start(), twoSentences.end() - 2);
        }
      } else {
        while (firstSentence.find()) {
          description = description.substring(firstSentence.start(), firstSentence.end() - 2);
          log.info("Description size {}", description.length());
        }
      }
      log.info(
          "[FINISH] Parsing tickers from wikipedia.org, time={}ms",
          (System.currentTimeMillis() - startTime));
    } catch (WikipediaParserException | IOException e) {
      throw new WikipediaParserException("WikipediaParser exception");
    }
    return description.replaceAll("\\[\\d+\\]", "");
  }
}

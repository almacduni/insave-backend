package org.save.controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.save.model.dto.search.CompanyInfoResponse;
import org.save.model.dto.search.OverallCompanyDescriptionResponse;
import org.save.service.serach.CompanyDescriptionService;
import org.save.service.serach.PageableSearchResponse;
import org.save.service.serach.SearchHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyDescriptionController {

  private final CompanyDescriptionService companyDescriptionService;
  private final SearchHistoryService searchHistoryService;

  @GetMapping("/{ticker}")
  public ResponseEntity<CompanyInfoResponse> getCompanyInfo(
      @PathVariable String ticker, Principal principal) {
    searchHistoryService.addToHistory(ticker, principal);
    return new ResponseEntity<>(companyDescriptionService.getCompanyInfo(ticker), HttpStatus.OK);
  }

  @GetMapping("/{ticker}/description")
  public ResponseEntity<OverallCompanyDescriptionResponse> getOverallCompanyDescription(
      @PathVariable String ticker) {
    OverallCompanyDescriptionResponse companyDescription =
        companyDescriptionService.getOverallCompanyDescription(ticker.toUpperCase());
    return new ResponseEntity<>(companyDescription, HttpStatus.OK);
  }

  @GetMapping("/search")
  public ResponseEntity<PageableSearchResponse> search(
      @RequestParam String ticker, @RequestParam Integer page, @RequestParam Integer pageLimit) {
    PageableSearchResponse search = companyDescriptionService.search(ticker, page, pageLimit);
    return new ResponseEntity<>(search, HttpStatus.OK);
  }
}

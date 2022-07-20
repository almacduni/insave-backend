package org.save.controller;

import java.util.List;
import java.util.Map;
import org.save.model.dto.search.CommonSearchRequestDto;
import org.save.service.CommonSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

  private final CommonSearchService commonSearchService;

  public SearchController(CommonSearchService commonSearchService) {
    this.commonSearchService = commonSearchService;
  }

  @GetMapping
  public Map<String, List<? extends CommonSearchRequestDto>> search(
      @RequestParam String searchPhrase) throws Exception {
    return commonSearchService.search(searchPhrase);
  }
}

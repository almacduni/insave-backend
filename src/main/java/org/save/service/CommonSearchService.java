package org.save.service;

import java.util.List;
import java.util.Map;
import org.save.model.dto.search.CommonSearchRequestDto;

public interface CommonSearchService {

  Map<String, List<? extends CommonSearchRequestDto>> search(String searchPhrase) throws Exception;
}

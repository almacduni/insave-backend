package org.save.service.serach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.save.model.dto.search.CommonSearchRequestDto;
import org.save.model.dto.search.CompanySearchRequestDto;
import org.save.model.dto.search.PlaylistSearchRequestDto;
import org.save.model.dto.search.UserSearchRequestDto;
import org.save.model.entity.social.playlist.Ticker;
import org.save.model.enums.CategoryEnum;
import org.save.repo.TickerRepository;
import org.save.repo.UserRepository;
import org.save.repo.playlist.PlaylistRepository;
import org.save.service.CommonSearchService;
import org.save.util.mapper.SearchRequestMapper;
import org.springframework.stereotype.Service;

@Service
public class CommonSearchServiceImpl implements CommonSearchService {

  public static final int SIZE_OF_THREAD_POOL = 3;
  private static final ExecutorService executorService =
      Executors.newFixedThreadPool(SIZE_OF_THREAD_POOL);

  private final TickerRepository tickerRepository;
  private final PlaylistRepository playlistRepository;
  private final UserRepository userRepository;
  private final SearchRequestMapper searchRequestMapper;

  public CommonSearchServiceImpl(
      TickerRepository tickerRepository,
      PlaylistRepository playlistRepository,
      UserRepository userRepository,
      SearchRequestMapper searchRequestMapper) {
    this.tickerRepository = tickerRepository;
    this.playlistRepository = playlistRepository;
    this.userRepository = userRepository;
    this.searchRequestMapper = searchRequestMapper;
  }

  @Override
  public Map<String, List<? extends CommonSearchRequestDto>> search(String searchPhrase)
      throws Exception {
    if (searchPhrase.isEmpty()) {
      return null;
    }
    Map<String, List<? extends CommonSearchRequestDto>> searchMap = new HashMap<>();
    executeAsyncTasksAndFillSearchMap(searchMap, searchPhrase);
    return searchMap;
  }

  private void executeAsyncTasksAndFillSearchMap(
      Map<String, List<? extends CommonSearchRequestDto>> searchMap, String searchPhrase)
      throws ExecutionException, InterruptedException {
    Callable<List<UserSearchRequestDto>> callableUserSearchRequestDtos =
        () -> getUsersBySearchPhrase(searchPhrase);
    Callable<List<PlaylistSearchRequestDto>> callablePlaylistSearchRequestDtos =
        () -> getPlaylistsBySearchPhrase(searchPhrase);
    Callable<CompanySearchRequestDto> callableCompanySearchRequestDto =
        () -> getTickerBySearchPhrase(searchPhrase);

    Future<List<UserSearchRequestDto>> futureUserSearchRequestDtos =
        executorService.submit(callableUserSearchRequestDtos);
    Future<List<PlaylistSearchRequestDto>> futurePlaylistSearchRequestDtos =
        executorService.submit(callablePlaylistSearchRequestDtos);
    Future<CompanySearchRequestDto> futureCompanySearchRequestDto =
        executorService.submit(callableCompanySearchRequestDto);

    if (futureCompanySearchRequestDto.get() != null) {
      fillSearchMap(
          CategoryEnum.COMPANIES.getCategory(),
          List.of(futureCompanySearchRequestDto.get()),
          searchMap);
    }
    fillSearchMap(
        CategoryEnum.PLAYLISTS.getCategory(), futurePlaylistSearchRequestDtos.get(), searchMap);
    fillSearchMap(CategoryEnum.USERS.getCategory(), futureUserSearchRequestDtos.get(), searchMap);
  }

  private void fillSearchMap(
      String category,
      List<? extends CommonSearchRequestDto> listOfSearchObjects,
      Map<String, List<? extends CommonSearchRequestDto>> searchMap) {
    searchMap.put(category, listOfSearchObjects);
  }

  private List<UserSearchRequestDto> getUsersBySearchPhrase(String searchPhrase) {
    List<UserSearchRequestDto> searchRequestDtos = new ArrayList<>();
    userRepository
        .findAllByUsername(searchPhrase)
        .forEach(
            user ->
                searchRequestDtos.add(searchRequestMapper.mapUserSearchRequestDtoFromUser(user)));
    return searchRequestDtos;
  }

  private List<PlaylistSearchRequestDto> getPlaylistsBySearchPhrase(String searchPhrase) {
    List<PlaylistSearchRequestDto> searchRequestDtos = new ArrayList<>();
    playlistRepository
        .findAllByTitle(searchPhrase)
        .forEach(
            playlist ->
                searchRequestDtos.add(
                    searchRequestMapper.mapPlaylistSearchRequestDtoFromPlaylist(playlist)));
    return searchRequestDtos;
  }

  private CompanySearchRequestDto getTickerBySearchPhrase(String searchPhrase) {
    Optional<Ticker> ticker = tickerRepository.findTickerByName(searchPhrase);
    return ticker.map(searchRequestMapper::mapCompanyFromTicker).orElse(null);
  }
}

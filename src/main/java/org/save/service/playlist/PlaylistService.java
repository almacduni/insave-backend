package org.save.service.playlist;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.save.client.ImageKitClient;
import org.save.exception.InvalidArgumentException;
import org.save.exception.NoSuchObjectException;
import org.save.exception.PlaylistCategoryExistsException;
import org.save.exception.PlaylistIsAlreadyInCategoryException;
import org.save.model.dto.playlist.CategoriesResponse;
import org.save.model.dto.playlist.CreateOrUpdatePlaylistRequest;
import org.save.model.dto.playlist.CreatePlaylistCategoryRequest;
import org.save.model.dto.playlist.ExplorePageablePlaylistResponse;
import org.save.model.dto.playlist.ExplorePlaylistResponse;
import org.save.model.dto.playlist.PageablePlaylistResponse;
import org.save.model.dto.playlist.PlaylistCategoryResponse;
import org.save.model.dto.playlist.PlaylistResponse;
import org.save.model.entity.common.User;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.entity.social.playlist.PlaylistCategory;
import org.save.model.entity.social.playlist.PlaylistDailyStatistic;
import org.save.model.entity.social.playlist.Ticker;
import org.save.repo.TickerRepository;
import org.save.repo.UserRepository;
import org.save.repo.playlist.PlaylistCategoryRepository;
import org.save.repo.playlist.PlaylistRepository;
import org.save.util.mapper.CategoriesResponseMapper;
import org.save.util.mapper.PlaylistMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService {

  private static final String CATEGORY_SORT_PARAM = "serialNumber";
  private static final String PLAYLIST_SORT_PARAM = "openings";
  private static final Integer MIN_SIZE_OF_TEXT = 3;
  private static final Integer MAX_SIZE_OF_TICKERS = 30;

  private final PlaylistRepository playlistRepository;
  private final PlaylistCategoryRepository playlistCategoryRepository;
  private final TickerRepository tickerRepository;
  private final UserRepository userRepository;
  private final ImageKitClient imageKitClient;

  private final CategoriesResponseMapper categoriesResponseMapper;
  private final PlaylistMapper playListMapper;

  public CategoriesResponse getCategories(Integer page, Integer pageLimit, Integer playlistLimit) {
    if (page <= 0 || pageLimit <= 0 || playlistLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from 1. Provided: "
              + page
              + ". Page limit minimal value is 1. Provided: "
              + pageLimit);
    }
    PageRequest categoryRequest = PageRequest.of(page - 1, pageLimit, Sort.by(CATEGORY_SORT_PARAM));
    PageRequest categoryPlaylistRequest =
        PageRequest.of(0, playlistLimit, Sort.by(PLAYLIST_SORT_PARAM).descending());
    Page<PlaylistCategory> categories = playlistCategoryRepository.findAll(categoryRequest);

    List<PlaylistCategoryResponse> categoriesResponse =
        categories.stream()
            .map(
                category ->
                    categoriesResponseMapper.convertToCategoryResponse(
                        category,
                        playlistRepository.findAllByCategories(category, categoryPlaylistRequest)
                            .stream()
                            .collect(Collectors.toList())))
            .collect(Collectors.toList());

    return CategoriesResponse.builder()
        .categories(categoriesResponse)
        .currentPage(categories.getNumber() + 1)
        .totalCount(categories.getTotalElements())
        .offset(categories.getSize())
        .build();
    //        categories.forEach(category -> {
    //            if (category.getCategory().equals("Top")) {
    //                category.setPlaylists(playlistRepository.findTopPlaylist());
    //            } else if (category.getCategory().equals("Trending")) {
    //                category.setPlaylists(playlistRepository.findTrendingPlaylist());
    //            } else {
    //                category.setPlaylists(playlistRepository.findAllByCategories(category,
    // Sort.by("openings").descending()));
    //            }});
    //            //TODO change this on above
    //            category.setPlaylists(playlistRepository.findAllByCategories(category,
    // categoryPlaylistRequest));
    //        });
  }

  public PlaylistCategoryResponse createCategory(CreatePlaylistCategoryRequest categoryRequest) {
    String name = categoryRequest.getName();
    if (playlistCategoryRepository.existsByCategory(name)) {
      throw new PlaylistCategoryExistsException("category with name is exist: " + name);
    }

    PlaylistCategory playlistCategory = new PlaylistCategory();
    int serialNumber = Math.toIntExact(playlistCategoryRepository.count());
    playlistCategory.setCategory(name);
    playlistCategory.setSerialNumber(++serialNumber);
    playlistCategoryRepository.save(playlistCategory);

    return playListMapper.convertToCategoryResponse(playlistCategory);
  }

  // TODO сheck for tickers count on front(less then 30)
  public void createPlaylist(
      CreateOrUpdatePlaylistRequest playlistRequest,
      MultipartFile multipleFile,
      Principal principal) {
    validatePlaylist(playlistRequest);

    Set<Ticker> tickers = new HashSet<>(playlistRequest.getTickers().size());
    playlistRequest
        .getTickers()
        .forEach(
            id ->
                tickers.add(
                    tickerRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new NoSuchObjectException("there is no ticker with id: " + id))));

    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new NoSuchObjectException("there is no authorized user"));

    String pictureLink = uploadFileToStorage(playlistRequest.getTitle(), multipleFile);
    Playlist playList =
        new Playlist(
            playlistRequest.getTitle(),
            playlistRequest.getDescription(),
            pictureLink,
            new ArrayList<>(tickers));
    playList.setTickersNumber(tickers.size());
    playList.setStatistic(new ArrayList<>());
    setTapsCount(playList);
    playList.setUser(user);
    playlistRepository.save(playList);
  }

  public void updatePlaylist(
      CreateOrUpdatePlaylistRequest playlistRequest,
      Long playlistId,
      MultipartFile multipleFile,
      Principal principal) {
    validatePlaylist(playlistRequest);

    Playlist playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no such playlist with id provided: " + playlistId));
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new NoSuchObjectException("there is no authorized user"));
    if (!user.getId().equals(playlist.getUser().getId())) {
      throw new InvalidArgumentException("you dont have needed authorities");
    }

    Set<Ticker> newTickers = new HashSet<>();
    playlistRequest
        .getTickers()
        .forEach(
            id ->
                newTickers.add(
                    tickerRepository
                        .findById(id)
                        .orElseThrow(
                            () ->
                                new NoSuchObjectException(
                                    "there is no ticker with provided id: " + id))));
    playlist.setTickers(new ArrayList<>(newTickers));
    playlist.setTitle(playlistRequest.getTitle());
    playlist.setDescription(playlistRequest.getDescription());
    String pictureLink = uploadFileToStorage(playlistRequest.getTitle(), multipleFile);
    playlist.setImageURL(pictureLink);

    playlistRepository.save(playlist);
  }

  private String uploadFileToStorage(String title, MultipartFile multipleFile) {
    String pictureLink = null;
    if (multipleFile != null) {
      Resource resource = multipleFile.getResource();
      String fileName = "picture";
      String filePath = "playlists/playlist_" + title;
      pictureLink = imageKitClient.uploadFileToStorage(resource, fileName, filePath, false);
    }
    return pictureLink;
  }

  private void setTapsCount(Playlist playlist) {
    playlist.setOpenings(playlist.getOpenings() + 1L);
    if (playlist.getStatistic().contains(new PlaylistDailyStatistic(LocalDate.now()))) {
      int lastIndex = playlist.getStatistic().size() - 1; // last index - current day
      PlaylistDailyStatistic trending = playlist.getStatistic().get(lastIndex);
      trending.setTapsCount(trending.getTapsCount() + 1L);
    } else {
      PlaylistDailyStatistic trending = new PlaylistDailyStatistic(LocalDate.now(), 1L);
      playlist.getStatistic().add(trending);
    }
  }

  public void addPlaylistToCategory(Long categoryId, Long playlistId) {
    PlaylistCategory category =
        playlistCategoryRepository
            .findById(categoryId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no such category with id provided: " + categoryId));
    Playlist playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no such playlist with id provided: " + playlistId));
    if (category.getPlaylists().contains(playlist)) {
      throw new PlaylistIsAlreadyInCategoryException(
          "provided playlist is already in category with id: " + categoryId);
    }

    category.getPlaylists().add(playlist);
    playlistCategoryRepository.save(category);
  }

  // TODO count number of tickers on front
  public void addTickersToPlaylist(List<Long> tickersId, Long playlistId, Principal principal) {
    Playlist playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no such playlist with id provided: " + playlistId));
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new NoSuchObjectException("there is no authorized user"));
    if (!user.getId().equals(playlist.getUser().getId())) {
      throw new InvalidArgumentException("you dont have needed authorities");
    }
    int tickersNumber = playlist.getTickersNumber();
    if (tickersNumber == 30) {
      throw new InvalidArgumentException("you cant add more tickers in playlist");
    }

    List<Ticker> playlistTickers = playlist.getTickers();
    Set<Ticker> newTickers = new HashSet<>();
    tickersId.forEach(
        id ->
            newTickers.add(
                tickerRepository
                    .findById(id)
                    .orElseThrow(
                        () ->
                            new NoSuchObjectException(
                                "there is no ticker with provided id: " + id))));
    for (Ticker ticker : newTickers) {
      if (!playlistTickers.contains(ticker) && tickersNumber <= 30) {
        playlistTickers.add(ticker);
        tickersNumber++;
      }
    }
    playlist.setTickersNumber(tickersNumber);
    playlist.setTickers(playlistTickers);

    playlistRepository.save(playlist);
  }

  // @Cacheable(cacheManager="cacheManager", cacheNames = "play_list")
  public PageablePlaylistResponse getAllPlaylists(Integer page, Integer pageLimit) {
    if (page <= 0 || pageLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from 1. Provided: "
              + page
              + ". Page limit minimal value is 1. Provided: "
              + pageLimit);
    }
    PageRequest playlistRequest =
        PageRequest.of(page - 1, pageLimit, Sort.by("openings").descending());
    Page<Playlist> resultList = playlistRepository.findAll(playlistRequest);
    List<PlaylistResponse> response =
        resultList.stream()
            .map(playListMapper::convertToPlayListResponse)
            .collect(Collectors.toList());

    return PageablePlaylistResponse.builder()
        .playlists(response)
        .currentPage(resultList.getNumber() + 1)
        .totalCount(resultList.getTotalElements())
        .offset(resultList.getSize())
        .build();
  }

  /**
   * method used after get Categories request to get other playlist in each category
   *
   * @param id category id
   * @param page page of Pageable request
   * @param pageLimit limit of playlists in explore
   * @return list of mapped playlists
   */
  public ExplorePageablePlaylistResponse getPlaylistsByCategory(
      Long id, Integer page, Integer pageLimit) {
    PlaylistCategory category =
        playlistCategoryRepository
            .findById(id)
            .orElseThrow(
                () -> new NoSuchObjectException("there is no category with provided id: " + id));

    if (page <= 0 || pageLimit <= 0) {
      throw new InvalidArgumentException(
          "Page starts from 1. Provided: "
              + page
              + ". Page limit minimal value is 1. Provided: "
              + pageLimit);
    }
    PageRequest playlistRequest =
        PageRequest.of(page - 1, pageLimit, Sort.by(PLAYLIST_SORT_PARAM).descending());

    Page<Playlist> playlists = playlistRepository.findAllByCategories(category, playlistRequest);

    List<ExplorePlaylistResponse> resultList =
        playlists.stream()
            .map(categoriesResponseMapper::convertToPlayListResponse)
            .collect(Collectors.toList());

    return ExplorePageablePlaylistResponse.builder()
        .playlists(resultList)
        .currentPage(playlists.getNumber() + 1)
        .totalCount(playlists.getTotalElements())
        .offset(playlists.getSize())
        .build();
  }

  public PlaylistResponse getPlaylistById(Long playListId) {
    final Playlist playList =
        playlistRepository
            .findById(playListId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no playlist with provided id: " + playListId));
    setTapsCount(playList);

    return playListMapper.convertToPlayListResponse(playList);
  }

  public boolean deletePlaylist(Long playlistId, Principal principal) {
    Playlist playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no playlist with provided id: " + playlistId));
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new NoSuchObjectException("there is no authorized user"));
    if (!user.getId().equals(playlist.getUser().getId())) {
      throw new InvalidArgumentException("you dont have needed authorities");
    }

    List<PlaylistCategory> playlistCategories = playlist.getCategories();
    playlistCategories.forEach(
        category -> {
          category.getPlaylists().remove(playlist);
          playlistCategoryRepository.save(category);
        });

    List<Ticker> tickers = playlist.getTickers();
    tickers.clear();

    playlistRepository.delete(playlist);
    return true;
  }

  public void deleteTicker(Long tickerId, Long playlistId, Principal principal) {

    Playlist playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException(
                        "there is no playlist with provided id: " + playlistId));
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new NoSuchObjectException("there is no authorized user"));
    if (!user.getId().equals(playlist.getUser().getId())) {
      throw new InvalidArgumentException("you dont have needed authorities");
    }

    Ticker ticker =
        tickerRepository
            .findById(tickerId)
            .orElseThrow(
                () ->
                    new NoSuchObjectException("there is no ticker with provided id: " + tickerId));

    if (playlist.getTickers().contains(ticker)) {
      playlist.getTickers().remove(ticker);
      playlistRepository.save(playlist);
    }
  }

  private void validatePlaylist(CreateOrUpdatePlaylistRequest playlistRequest) {
    validateTextParam(
        playlistRequest.getTitle(), "Playlist title can't be null or less then 3 symbols");
    validateTextParam(
        playlistRequest.getDescription(),
        "Playlist description can't be null or less then 3 symbols");

    if (playlistRequest.getTickers().isEmpty()) {
      throw new InvalidArgumentException("Tickers cant be null in playlist");
    }
    if (playlistRequest.getTickers().size() > MAX_SIZE_OF_TICKERS) {
      throw new InvalidArgumentException(
          "You cant add more than 30 tickers, provided: " + playlistRequest.getTickers().size());
    }
  }

  private void validateTextParam(String textParam, String errorMessage) {
    if (textParam == null || textParam.length() < MIN_SIZE_OF_TEXT) {
      throw new InvalidArgumentException(errorMessage);
    }
  }
}

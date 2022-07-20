package org.save.util.mapper;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.model.dto.playlist.ExplorePlaylistResponse;
import org.save.model.dto.playlist.ExploreTickerResponse;
import org.save.model.dto.playlist.PlaylistCategoryResponse;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.entity.social.playlist.PlaylistCategory;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.stereotype.Component;

/** converts categories and playlist provided for explore */
@Component
@RequiredArgsConstructor
public class CategoriesResponseMapper {

  public ExploreTickerResponse convertToTickerResponse(Ticker ticker) {
    return new ExploreTickerResponse(ticker.getName());
  }

  public PlaylistCategoryResponse convertToCategoryResponse(
      PlaylistCategory category, List<Playlist> playlists) {
    List<ExplorePlaylistResponse> playlistResponseList =
        new ArrayList<>(category.getPlaylists().size());
    playlists.forEach(playlist -> playlistResponseList.add(convertToPlayListResponse(playlist)));

    return new PlaylistCategoryResponse(
        category.getId(), category.getCategory(), category.getSerialNumber(), playlistResponseList);
  }

  public ExplorePlaylistResponse convertToPlayListResponse(Playlist playList) {
    List<ExploreTickerResponse> tickerResponseList = new ArrayList<>(playList.getTickers().size());
    List<Ticker> tickers;
    if (playList.getTickers().size() >= 4) { // max number of tickers in response is 4
      tickers = playList.getTickers().subList(0, 4);
    } else {
      tickers = playList.getTickers();
    }
    tickers.forEach(ticker -> tickerResponseList.add(convertToTickerResponse(ticker)));

    return new ExplorePlaylistResponse(
        playList.getId(),
        playList.getUser().getUsername(),
        playList.getTitle(),
        playList.getDescription(),
        playList.getImageURL(),
        tickerResponseList);
  }
}

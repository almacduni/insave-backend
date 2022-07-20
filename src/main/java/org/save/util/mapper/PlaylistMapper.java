package org.save.util.mapper;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.dto.playlist.PlaylistCategoryResponse;
import org.save.model.dto.playlist.PlaylistResponse;
import org.save.model.dto.playlist.TickerResponse;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.entity.social.playlist.PlaylistCategory;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaylistMapper {

  public TickerResponse convertToTickerResponse(Ticker ticker) {
    return new TickerResponse(
        ticker.getName(), ticker.getCompany(), ticker.getMarketCapitalization(), ticker.getAmg());
  }

  public PlaylistCategoryResponse convertToCategoryResponse(PlaylistCategory category) {

    return new PlaylistCategoryResponse(
        category.getId(), category.getCategory(), category.getSerialNumber());
  }

  public PlaylistResponse convertToPlayListResponse(Playlist playList) {

    List<TickerResponse> tickerResponseList = new ArrayList<>(playList.getTickers().size());
    List<Ticker> tickers = playList.getTickers();
    tickers.forEach(ticker -> tickerResponseList.add(convertToTickerResponse(ticker)));

    return new PlaylistResponse(
        playList.getId(),
        playList.getUser().getUsername(),
        playList.getTitle(),
        playList.getDescription(),
        playList.getImageURL(),
        tickerResponseList);
  }
}

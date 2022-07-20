package org.save.service.playlist;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.entity.social.playlist.PlaylistCategory;
import org.save.model.entity.social.playlist.PlaylistTrendingPoints;
import org.save.repo.playlist.PlaylistCategoryRepository;
import org.save.repo.playlist.PlaylistRepository;
import org.save.repo.playlist.PlaylistTopRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <code>CategoryService</code> class is needed to make scheduled sorting of playlists in categories
 * 'Top' and 'Trending'. sorting happens once per day.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class CategoryService {

  private final PlaylistCategoryRepository categoryRepository;
  private final PlaylistRepository playlistRepository;
  private final PlaylistTopRepository playlistTopRepository;

  /**
   * method <code>sortPlaylistsInCategoryTop</code> sorts playlist in 'Top' and 'Trending'
   * categories by user openings that was made during 3 and 30 days before current day and deletes
   * information about taps(openings) out of 30 days before current day
   */
  @Transactional
  @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
  public void sortPlaylistsInCategoryTopAndTrending() {
    PlaylistCategory topCategory = categoryRepository.findByCategory("Top");
    PlaylistCategory trendingCategory = categoryRepository.findByCategory("Trending");
    List<Playlist> playlists = playlistRepository.findAll();
    List<PlaylistTrendingPoints> playlistTopList = new ArrayList<>();

    playlists.forEach(
        playlist -> {
          PlaylistTrendingPoints playlistTop = new PlaylistTrendingPoints();
          playlistTop.setPlaylist(playlist);
          playlistTop.setTopPoints(
              playlistRepository
                  .findTaps(playlist.getId(), LocalDate.now().minus(3, ChronoUnit.DAYS))
                  .orElse(0L));
          playlistTop.setTrendingPoints(
              playlistRepository
                  .findTaps(playlist.getId(), LocalDate.now().minus(30, ChronoUnit.DAYS))
                  .orElse(0L));
          playlistRepository.deleteTapsByDate(
              playlist.getId(), LocalDate.now().minus(30, ChronoUnit.DAYS));
          playlistTopList.add(playlistTop);
        });

    for (int i = 0; i < playlistTopList.size(); i++) {
      playlistTopList.get(i).setId((long) i + 1);
    }

    trendingCategory.setPlaylists(playlists);
    topCategory.setPlaylists(playlists);
    playlistTopRepository.saveAll(playlistTopList);
    categoryRepository.save(topCategory);
    categoryRepository.save(trendingCategory);
  }
}

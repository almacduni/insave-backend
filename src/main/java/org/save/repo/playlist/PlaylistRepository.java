package org.save.repo.playlist;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.entity.social.playlist.PlaylistCategory;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

  @Query(
      value =
          "SELECT sum(taps_count) FROM playlist_daily_statistic "
              + "WHERE playlist_id = :playlistId and day >= :day",
      nativeQuery = true)
  Optional<Long> findTaps(@Param("playlistId") Long id, @Param("day") LocalDate date);

  @Modifying
  @Query(
      value =
          "DELETE FROM playlist_daily_statistic "
              + "WHERE playlist_id = :playlistId and day <= :day",
      nativeQuery = true)
  void deleteTapsByDate(@Param("playlistId") Long playlistId, @Param("day") LocalDate date);

  Page<Playlist> findAllByCategories(PlaylistCategory category, Pageable pageable);

  List<Playlist> findAllByTitle(String title);

  List<Playlist> findByTickers(Ticker ticker);

  Page<Playlist> findAll(Pageable pageable);

  boolean existsById(Long playlistId);
}

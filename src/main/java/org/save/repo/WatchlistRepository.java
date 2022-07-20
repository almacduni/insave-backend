package org.save.repo;

import java.util.Optional;
import org.save.model.entity.watchlist.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
  Optional<Watchlist> findWatchlistByUserId(@Param("userId") Long userId);
}

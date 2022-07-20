package org.save.repo;

import java.util.Optional;
import org.save.model.entity.watchlist.WatchlistItemDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistItemDescriptionRepository
    extends JpaRepository<WatchlistItemDescription, Long> {

  Optional<WatchlistItemDescription> findByTicker(String ticker);
}

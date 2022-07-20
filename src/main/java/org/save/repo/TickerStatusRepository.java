package org.save.repo;

import java.util.Optional;
import org.save.model.entity.social.playlist.TickerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TickerStatusRepository extends JpaRepository<TickerStatus, Long> {

  Optional<TickerStatus> findByTicker(String ticker);

  void deleteByTicker(String ticker);

  boolean existsByTicker(String ticker);
}

package org.save.repo;

import java.util.Optional;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TickerRepository extends JpaRepository<Ticker, Long> {

  @Query(value = "SELECT * FROM tickers " + "WHERE tickers.name = :ticker", nativeQuery = true)
  Optional<Ticker> findTickerByName(@Param("ticker") String ticker);

  @Modifying
  @Transactional
  @Query(value = "update tickers set amg = :amg where name = :ticker", nativeQuery = true)
  void setAmgByTickerName(Double amg, String ticker);

  boolean existsByName(String name);

  Optional<Ticker> findById(Long id);

  Page<Ticker> findAllByNameIgnoreCaseStartingWithOrCompanyIgnoreCaseStartingWith(
      String name, String company, Pageable pageable);
}

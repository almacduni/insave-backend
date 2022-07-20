package org.save.repo;

import org.save.model.entity.common.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

  Asset findByPortfolioIdAndTicker(Long portfolioId, String ticker);
}

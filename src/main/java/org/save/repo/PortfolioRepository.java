package org.save.repo;

import org.save.model.entity.common.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

  Portfolio getPortfolioByUserId(Long userId);
}

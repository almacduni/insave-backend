package org.save.repo;

import org.save.model.entity.tatum.ReferralTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralTransferRepository extends JpaRepository<ReferralTransfer, Long> {}

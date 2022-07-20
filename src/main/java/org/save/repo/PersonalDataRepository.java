package org.save.repo;

import org.save.model.entity.user.PersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PersonalDataRepository extends JpaRepository<PersonalData, Long> {

  @Modifying
  @Transactional
  @Query(
      value = "UPDATE personal_data SET tickers_search_history = :tickers WHERE user_id = :userId",
      nativeQuery = true)
  void addToHistory(Long userId, String tickers);

  PersonalData findPersonalDataByUserId(Long userId);
}

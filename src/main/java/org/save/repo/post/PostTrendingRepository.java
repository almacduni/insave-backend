package org.save.repo.post;

import org.save.model.entity.social.post.PostTrending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostTrendingRepository extends JpaRepository<PostTrending, Long> {

  @Query(value = "SELECT * FROM post_trendings " + "ORDER BY trending_id ", nativeQuery = true)
  Page<PostTrending> findPageable(Pageable pageable);
}

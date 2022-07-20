package org.save.repo.post;

import org.save.model.entity.social.post.PostForYouTrending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostForYouTrendingRepository extends JpaRepository<PostForYouTrending, Long> {

  @Query(
      value = "SELECT * FROM post_foryou_trending " + "ORDER BY trending_id ",
      nativeQuery = true)
  Page<PostForYouTrending> findPageable(Pageable pageable);
}

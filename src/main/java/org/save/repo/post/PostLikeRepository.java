package org.save.repo.post;

import java.util.Optional;
import org.save.model.entity.social.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  @Transactional(readOnly = true)
  Boolean existsByPostIdAndUserId(Long postId, Long userId);

  Optional<PostLike> findPostLikeByPostIdAndUserId(Long postId, Long userId);

  @Query(
      value = "SELECT COUNT(*) FROM post_likes " + "WHERE user_id = :userId ",
      nativeQuery = true)
  Long getCountLikesByUserId(@Param("userId") Long userId);

  @Query(value = "SELECT COUNT(*) FROM post_likes " + "WHERE post_id = :postId", nativeQuery = true)
  Integer getCountLikesByPostId(@Param("postId") Long postId);
}

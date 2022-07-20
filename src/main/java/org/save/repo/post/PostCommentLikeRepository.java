package org.save.repo.post;

import java.util.Optional;
import org.save.model.entity.social.post.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {

  @Transactional(readOnly = true)
  Boolean existsByCommentIdAndUserId(Long commentId, Long userId);

  Optional<PostCommentLike> findPostCommentLikeByCommentIdAndUserId(Long commentId, Long userId);

  @Query(
      value = "SELECT COUNT(*) FROM post_comments " + "WHERE post_id = :postId ",
      nativeQuery = true)
  Integer getCountCommentsByPostId(@Param("postId") Long postId);
}

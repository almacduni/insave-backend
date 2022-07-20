package org.save.repo.post;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import org.save.model.entity.common.User;
import org.save.model.entity.social.post.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
  Collection<PostComment> findAllByPostIdOrderByIdDesc(Long postId);

  @Query(
      value = "SELECT COUNT(*) FROM post_comments " + "WHERE user_id = :userId ",
      nativeQuery = true)
  Long getCountCommentsByUserId(@Param("userId") Long userId);

  @Query(
      value =
          "SELECT * FROM post_comments "
              + "WHERE user_id = :userId "
              + "and comment_id = :commentId",
      nativeQuery = true)
  Optional<PostComment> findByUser(
      @Param("userId") Long UserId, @Param("commentId") Long commentId);

  boolean existsById(Long commentId);

  Optional<Integer> countByDateAfterAndUser(Instant instant, User user);
}

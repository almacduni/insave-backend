package org.save.repo.post;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import javax.transaction.Transactional;
import org.save.model.entity.common.User;
import org.save.model.entity.social.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  @Query(
      value = "select * from posts " + "where user_id = :userId " + "and post_id = :postId",
      nativeQuery = true)
  Optional<Post> findPostByUserIdAndPostId(
      @Param("userId") Long userId, @Param("postId") Long postId);

  Collection<Post> findAllByDateIsAfter(@Param("instant") Instant instant);

  @Transactional
  @Query(value = "SELECT * FROM posts " + "ORDER BY date DESC ", nativeQuery = true)
  Page<Post> findLimited(Pageable pageable);

  @Query(value = "SELECT COUNT(*) FROM posts " + "WHERE user_id = :userId ", nativeQuery = true)
  Long getCountPosts(@Param("userId") Long userId);

  @Query(
      value =
          "SELECT sum(likes_count) FROM post_daily_statistic "
              + "WHERE post_id = :postId and day >= :date",
      nativeQuery = true)
  Optional<Integer> countNumberOfLikes(@Param("postId") Long postId, @Param("date") LocalDate date);

  @Query(
      value =
          "SELECT sum(openings) FROM post_daily_statistic "
              + "WHERE post_id = :postId and day >= :date",
      nativeQuery = true)
  Optional<Integer> countOpenings(@Param("postId") Long postId, @Param("date") LocalDate date);

  @Query(
      value =
          "SELECT sum(comments_count) FROM post_daily_statistic "
              + "WHERE post_id = :postId and day >= :date",
      nativeQuery = true)
  Optional<Integer> countCommentsNumber(
      @Param("postId") Long postId, @Param("date") LocalDate date);

  @Modifying
  @Query(value = "DELETE FROM post_daily_statistic " + "WHERE day <= :date", nativeQuery = true)
  void deleteStatisticByDate(@Param("date") LocalDate date);

  Optional<Integer> countByDateAfterAndUser(Instant instant, User user);

  boolean existsById(Long postId);
}

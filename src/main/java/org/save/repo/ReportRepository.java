package org.save.repo;

import org.save.model.entity.social.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  boolean existsByUserIdAndPostId(Long userId, Long postId);

  boolean existsByUserIdAndPostCommentId(Long userId, Long postCommentId);
}

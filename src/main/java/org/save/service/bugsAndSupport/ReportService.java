package org.save.service.bugsAndSupport;

import static org.save.model.enums.ReportedObject.POST_COMMENT_REPORT;
import static org.save.model.enums.ReportedObject.POST_REPORT;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.exception.NoSuchObjectException;
import org.save.exception.ReportAlreadyExistException;
import org.save.model.dto.social.report.PostCommentReportRequest;
import org.save.model.dto.social.report.PostReportRequest;
import org.save.model.entity.common.User;
import org.save.model.entity.social.report.Report;
import org.save.model.enums.ReportType;
import org.save.model.enums.ReportedObject;
import org.save.repo.ReportRepository;
import org.save.repo.post.PostCommentRepository;
import org.save.repo.post.PostRepository;
import org.save.service.implementation.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository postReportRepository;
  private final PostCommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserService userService;

  public Report createPostReport(PostReportRequest request, Principal principal) {
    isObjectExist(POST_REPORT, request.getPostId());
    Report report = new Report();
    User user = userService.getUserByUsername(principal.getName());
    isPostReported(user.getId(), request.getPostId());

    report.setUserId(user.getId());
    report.setPostId(request.getPostId());
    report.setUserEmail(user.getEmail());
    report.setUsername(user.getUsername());
    report.setReportDate(ZonedDateTime.now().toInstant().toEpochMilli());
    report.setDescription(request.getDescription());
    report.setReportType(request.getReportType());
    report.setReportedObject(POST_REPORT);

    return postReportRepository.save(report);
  }

  public void createPostCommentReport(PostCommentReportRequest request, Principal principal) {
    isObjectExist(POST_COMMENT_REPORT, request.getPostCommentId());
    Report report = new Report();
    User user = userService.getUserByUsername(principal.getName());
    isPostCommentReported(user.getId(), request.getPostCommentId());

    report.setUserId(user.getId());
    report.setPostCommentId(request.getPostCommentId());
    report.setUserEmail(user.getEmail());
    report.setUsername(user.getUsername());
    report.setReportDate(ZonedDateTime.now().toInstant().toEpochMilli());
    report.setDescription(request.getDescription());
    report.setReportType(request.getReportType());
    report.setReportedObject(POST_COMMENT_REPORT);

    postReportRepository.save(report);
  }

  public List<String> getAllReportTypes() {
    return ReportType.getAllReportTypes();
  }

  private void isObjectExist(ReportedObject reportType, Long id) {
    switch (reportType) {
      case POST_REPORT:
        if (!postRepository.existsById(id)) {
          throw new NoSuchObjectException("There is no post with provided id: " + id);
        }
        break;
      case POST_COMMENT_REPORT:
        if (!commentRepository.existsById(id)) {
          throw new NoSuchObjectException("There is no comments with provided id: " + id);
        }
        break;
    }
  }

  private void isPostReported(Long userId, Long postId) {
    if (postReportRepository.existsByUserIdAndPostId(userId, postId)) {
      throw new ReportAlreadyExistException();
    }
  }

  private void isPostCommentReported(Long userId, Long postCommentId) {
    if (postReportRepository.existsByUserIdAndPostCommentId(userId, postCommentId)) {
      throw new ReportAlreadyExistException();
    }
  }

  public List<Report> getReports() {
    return new ArrayList<>(
        postReportRepository.findAll(Sort.by(Sort.DEFAULT_DIRECTION, "reportType")));
  }

  public Report getReport(Long reportId) {
    return postReportRepository
        .findById(reportId)
        .orElseThrow(() -> new NoSuchObjectException("Report does not exist"));
  }
}

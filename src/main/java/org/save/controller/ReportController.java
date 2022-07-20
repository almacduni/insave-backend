package org.save.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.save.model.dto.social.report.PostCommentReportRequest;
import org.save.model.dto.social.report.PostReportRequest;
import org.save.model.entity.social.report.Report;
import org.save.service.bugsAndSupport.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

  private final ReportService reportService;

  @Secured({"ROLE_SUPPORT", "ROLE_ADMIN"})
  @GetMapping
  public ResponseEntity<List<Report>> getReports() {
    return new ResponseEntity<>(reportService.getReports(), HttpStatus.OK);
  }

  @Secured({"ROLE_SUPPORT", "ROLE_ADMIN"})
  @GetMapping("/{id}")
  public ResponseEntity<Report> getReport(@PathVariable Long id) {
    return new ResponseEntity<>(reportService.getReport(id), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @GetMapping("/types")
  public ResponseEntity<List<String>> getAllReportTypes() {
    return new ResponseEntity<>(reportService.getAllReportTypes(), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PostMapping
  public ResponseEntity<Report> createPostReport(
      @RequestBody PostReportRequest reportRequest, Principal principal) {
    return new ResponseEntity<>(
        reportService.createPostReport(reportRequest, principal), HttpStatus.OK);
  }

  @Secured({"ROLE_USER", "ROLE_BANNED"})
  @PostMapping("/comment")
  public ResponseEntity<?> createPostCommentReport(
      @RequestBody PostCommentReportRequest reportRequest, Principal principal) {
    reportService.createPostCommentReport(reportRequest, principal);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}

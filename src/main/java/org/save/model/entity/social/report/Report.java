package org.save.model.entity.social.report;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.ReportType;
import org.save.model.enums.ReportedObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String description;
  private Long postId;
  private Long postCommentId;
  private Long userId;
  private String userEmail;
  private String username;
  private Long reportDate;
  private ReportedObject reportedObject;
  private ReportType reportType;
}

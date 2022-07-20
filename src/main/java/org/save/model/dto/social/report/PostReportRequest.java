package org.save.model.dto.social.report;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.save.model.enums.ReportType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReportRequest {

  @NotNull private Long postId;

  @Size(max = 840)
  private String description;

  @NotBlank private ReportType reportType;
}

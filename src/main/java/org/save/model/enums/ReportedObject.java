package org.save.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportedObject {
  POST_REPORT("post_report"),
  POST_COMMENT_REPORT("post_comment_report");

  @Getter private final String type;
}

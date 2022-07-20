package org.save.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportType {
  SPAM_OR_MISLEADING("Spam or Misleading"),
  ABUSIVE_OR_HARMFUL_CONTENT("Abusive or Harmful Content"),
  INAPPROPRIATE_ADS_OR_LINKS("Inappropriate Ads or Links"),
  IMPERSONATION("Impersonation");

  @Getter private final String type;

  public static List<String> getAllReportTypes() {
    return Arrays.stream(ReportType.values()).map(ReportType::getType).collect(Collectors.toList());
  }
}

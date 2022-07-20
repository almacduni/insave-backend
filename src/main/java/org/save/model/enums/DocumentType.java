package org.save.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentType {
  CARD_ID("CARD_ID"),
  PASSPORT("PASSPORT"),
  DRIVER_LICENSE("DRIVER_LICENSE");

  @Getter private final String documentType;
}

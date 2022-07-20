package org.save.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CategoryEnum {
  COMPANIES("companies"),
  PLAYLISTS("playlists"),
  USERS("users");

  @Getter private final String category;
}

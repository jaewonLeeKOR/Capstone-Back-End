package com.inha.capstone.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileCategory {
  IMAGE("image"),
  VIDEO("video"),
  AUDIO("audio"),
  GIF("gif"),
  APPLICATION("application"),
  UI_IMAGE("uiImage"),
  ;
  public final String name;
}

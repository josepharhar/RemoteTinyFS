package com.tinyfs.dao;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class DiskKey {
  private final String username;
  private final String diskname;
}

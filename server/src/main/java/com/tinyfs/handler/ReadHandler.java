package com.tinyfs.handler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.DiskKey;
import com.tinyfs.dao.HighlyAvailableDiskAdapter;
import com.tinyfs.exception.UnregisteredDiskException;

@Component
public class ReadHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final HighlyAvailableDiskAdapter diskAdapter;

  @Inject
  public ReadHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final HighlyAvailableDiskAdapter diskAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.diskAdapter = diskAdapter;
  }

  public byte[] performReadRequest(
      final String sessionId,
      final String diskname,
      final int offset,
      final int size) throws UnregisteredDiskException {
    String registeredUser = clientCacheAdapter
      .getRegisteredUsername(sessionId);

    if (StringUtils.isEmpty(diskname) || StringUtils.isEmpty(registeredUser)) {
      throw new UnregisteredDiskException();
    }

    return diskAdapter.readFromDisk(
      DiskKey.builder()
        .username(registeredUser)
        .diskname(diskname)
        .build(),
      offset,
      size);
  }
}

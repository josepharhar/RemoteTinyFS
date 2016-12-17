package com.tinyfs.handler;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.DiskKey;
import com.tinyfs.dao.HighlyAvailableDiskAdapter;
import com.tinyfs.exception.UnregisteredDiskException;

@Component
public class WriteHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final HighlyAvailableDiskAdapter diskAdapter;

  @Inject
  public WriteHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final HighlyAvailableDiskAdapter diskAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.diskAdapter = diskAdapter;
  }

  public void performWriteRequest(
      final String sessionId,
      final String diskname,
      final byte[] message,
      final int offset) throws UnregisteredDiskException {
    String registeredUser = clientCacheAdapter
      .getRegisteredUsername(sessionId);

    if (StringUtils.isEmpty(diskname) || StringUtils.isEmpty(registeredUser)) {
      throw new UnregisteredDiskException();
    }

    diskAdapter.writeToDisk(
      DiskKey.builder()
        .username(registeredUser)
        .diskname(diskname)
        .build(),
      message,
      offset);
  }
}

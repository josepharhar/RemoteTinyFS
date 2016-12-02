package com.tinyfs.handler;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.FileKey;
import com.tinyfs.dao.HighlyAvailableFileAdapter;
import com.tinyfs.exception.UnregisteredFileException;

@Component
public class WriteHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final HighlyAvailableFileAdapter fileAdapter;

  @Inject
  public WriteHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final HighlyAvailableFileAdapter fileAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.fileAdapter = fileAdapter;
  }

  public void performWriteRequest(
      final String sessionId,
      final String filename,
      final byte[] message,
      final int offset) throws UnregisteredFileException {
    String registeredUser = clientCacheAdapter
      .getRegisteredUsername(sessionId);

    if (StringUtils.isEmpty(filename) || StringUtils.isEmpty(registeredUser)) {
      throw new UnregisteredFileException();
    }

    fileAdapter.writeToFile(
      FileKey.builder()
        .username(registeredUser)
        .fileName(filename)
        .build(),
      message,
      offset);
  }
}

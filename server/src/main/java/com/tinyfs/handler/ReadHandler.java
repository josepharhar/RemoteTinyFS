package com.tinyfs.handler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.FileKey;
import com.tinyfs.dao.HighlyAvailableFileAdapter;
import com.tinyfs.exception.UnregisteredFileException;

@Component
public class ReadHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final HighlyAvailableFileAdapter fileAdapter;

  @Inject
  public ReadHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final HighlyAvailableFileAdapter fileAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.fileAdapter = fileAdapter;
  }

  public byte[] performReadRequest(
      final String sessionId,
      final String filename,
      final int offset,
      final int size) throws UnregisteredFileException {
    String registeredUser = clientCacheAdapter
      .getRegisteredUsername(sessionId);

    if (StringUtils.isEmpty(filename) || StringUtils.isEmpty(registeredUser)) {
      throw new UnregisteredFileException();
    }

    return fileAdapter.readFromFile(
      FileKey.builder()
        .username(registeredUser)
        .fileName(filename)
        .build(),
      offset,
      size);
  }
}

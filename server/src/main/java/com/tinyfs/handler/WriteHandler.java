package com.tinyfs.handler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.exception.UnregisteredFileSystemException;

@Component
public class WriteHandler {

  private final ClientCacheAdapter clientCacheAdapter;

  @Inject
  public WriteHandler(
      final ClientCacheAdapter clientCacheAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
  }

  public void performWriteRequest(
      final String sessionId,
      final String fileSystem,
      final String message) throws UnregisteredFileSystemException {
    List<String> allowedFileSystems = clientCacheAdapter
      .getRegisteredFileSystems(sessionId);

    if (StringUtils.isEmpty(fileSystem) || !allowedFileSystems.contains(fileSystem)) {
      throw new UnregisteredFileSystemException();
    }

    // TODO actually do something with this message
    System.out.printf(
      "FS: %s%nMessage: %s",
      fileSystem,
      message);
  }
}
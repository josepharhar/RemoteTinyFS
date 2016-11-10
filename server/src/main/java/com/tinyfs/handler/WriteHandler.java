package com.tinyfs.handler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.FileAdapter;
import com.tinyfs.exception.UnregisteredFileSystemException;

@Component
public class WriteHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final FileAdapter fileAdapter;

  @Inject
  public WriteHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final FileAdapter fileSystemAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.fileAdapter = fileSystemAdapter;
  }

  public void performWriteRequest(
      final String sessionId,
      final String fileSystem,
      final byte[] message,
      final int offset) throws UnregisteredFileSystemException {
    List<String> allowedFileSystems = clientCacheAdapter
      .getRegisteredFileSystems(sessionId);

    if (StringUtils.isEmpty(fileSystem) || !allowedFileSystems.contains(fileSystem)) {
      throw new UnregisteredFileSystemException();
    }

    fileAdapter.writeToFile(fileSystem, message, offset);
  }
}
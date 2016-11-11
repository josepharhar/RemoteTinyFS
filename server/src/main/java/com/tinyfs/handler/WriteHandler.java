package com.tinyfs.handler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.FileAdapter;
import com.tinyfs.exception.UnregisteredFileException;

@Component
public class WriteHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final FileAdapter fileAdapter;

  @Inject
  public WriteHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final FileAdapter fileAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.fileAdapter = fileAdapter;
  }

  public void performWriteRequest(
      final String sessionId,
      final String filename,
      final byte[] message,
      final int offset) throws UnregisteredFileException {
    List<String> allowedFiles = clientCacheAdapter
      .getRegisteredFiles(sessionId);

    if (StringUtils.isEmpty(filename) || !allowedFiles.contains(filename)) {
      throw new UnregisteredFileException();
    }

    fileAdapter.writeToFile(filename, message, offset);
  }
}

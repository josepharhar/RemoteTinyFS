package com.tinyfs.handler;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.FileAdapter;
import com.tinyfs.exception.UnregisteredFileException;

@Component
public class ReadHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final FileAdapter fileAdapter;

  @Inject
  public ReadHandler(
      final ClientCacheAdapter clientCacheAdapter,
      final FileAdapter fileAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.fileAdapter = fileAdapter;
  }

  public byte[] performReadRequest(
      final String sessionId,
      final String filename,
      final int offset,
      final int size) throws UnregisteredFileException {
    List<String> allowedFiles = clientCacheAdapter
      .getRegisteredFiles(sessionId);

    if (StringUtils.isEmpty(filename) || !allowedFiles.contains(filename)) {
      throw new UnregisteredFileException();
    }

    return fileAdapter.readFromFile(filename, offset, size);
  }
}

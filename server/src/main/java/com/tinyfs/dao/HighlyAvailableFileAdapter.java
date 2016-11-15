package com.tinyfs.dao;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class HighlyAvailableFileAdapter implements FileAdapter {

  @SuppressWarnings("unused")
  private final HighlyAvailableRemovalListener highlyAvailableRemovalListener;
  private final StorageBasedFileAdapter storageBasedFileAdapter;

  private LoadingCache<String, Byte[]> fileSystemCache;

  public HighlyAvailableFileAdapter(
      final HighlyAvailableRemovalListener highlyAvailableRemovalListener,
      final StorageBasedFileAdapter storageBasedFileAdapter) {
    this.highlyAvailableRemovalListener = highlyAvailableRemovalListener;
    this.storageBasedFileAdapter = storageBasedFileAdapter;

    this.fileSystemCache =
      CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .removalListener(highlyAvailableRemovalListener)
        .<String, Byte[]> build(
          CacheLoader.from(this::newFile));
  }

  public void writeToFile(
      final String fileSystemName,
      final byte[] message,
      final int offset) {
    Byte[] file = fileSystemCache.getUnchecked(fileSystemName);

    for (int i = 0; i < message.length; i++) {
      file[offset + i] = message[i];
    }
  }

  public byte[] readFromFile(
      final String fileSystemName,
      final int offset,
      final int size) {
    Byte[] file = fileSystemCache.getUnchecked(fileSystemName);

    byte[] message = new byte[size];
    for (int i = 0; i < size; i++) {
      message[i] = file[offset + i];
    }

    return message;
  }

  public void closeFile(final String fileName) {
    fileSystemCache.invalidate(fileName);
  }

  private Byte[] newFile(final String fileName) {
    return Arrays
      .asList(storageBasedFileAdapter.readFromFile(fileName, 0, MAX_FILE_SIZE))
      .toArray(new Byte[0]);
  }
}

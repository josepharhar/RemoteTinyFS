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

  private LoadingCache<String, Byte[]> fileCache;

  public HighlyAvailableFileAdapter(
      final HighlyAvailableRemovalListener highlyAvailableRemovalListener,
      final StorageBasedFileAdapter storageBasedFileAdapter) {
    this.highlyAvailableRemovalListener = highlyAvailableRemovalListener;
    this.storageBasedFileAdapter = storageBasedFileAdapter;

    this.fileCache =
      CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .removalListener(highlyAvailableRemovalListener)
        .<String, Byte[]> build(
          CacheLoader.from(this::newFile));
  }

  public void writeToFile(
      final String fileName,
      final byte[] message,
      final int offset) {
    Byte[] file = fileCache.getUnchecked(fileName);

    for (int i = 0; i < message.length; i++) {
      file[offset + i] = message[i];
    }
  }

  public byte[] readFromFile(
      final String fileName,
      final int offset,
      final int size) {
    Byte[] file = fileCache.getUnchecked(fileName);

    byte[] message = new byte[size];
    for (int i = 0; i < size; i++) {
      message[i] = file[offset + i];
    }

    return message;
  }

  public void closeFile(final String fileName) {
    fileCache.invalidate(fileName);
  }

  private Byte[] newFile(final String fileName) {
    return Arrays
      .asList(storageBasedFileAdapter.readFromFile(fileName, 0, MAX_FILE_SIZE))
      .toArray(new Byte[0]);
  }
}

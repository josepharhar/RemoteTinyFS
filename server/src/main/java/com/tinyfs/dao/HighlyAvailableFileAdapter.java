package com.tinyfs.dao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class HighlyAvailableFileAdapter implements FileAdapter {

  @SuppressWarnings("unused")
  private final HighlyAvailableRemovalListener highlyAvailableRemovalListener;
  private final StorageBasedFileAdapter storageBasedFileAdapter;

  private LoadingCache<FileKey, Byte[]> fileCache;

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
        .<FileKey, Byte[]> build(
          CacheLoader.from(this::newFile));
  }

  public void writeToFile(
      final FileKey fileKey,
      final byte[] message,
      final int offset) {
    try {
      Byte[] file = fileCache.get(fileKey);
      if (offset + message.length > file.length) {
        throw new RuntimeException("writing out of bounds to file. file.length: " + file.length + ", offset: " + offset + ", message.length: " + message.length);
      }

      for (int i = 0; i < message.length; i++) {
        file[offset + i] = message[i];
      }
    } catch (ExecutionException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }
  }

  public byte[] readFromFile(
      final FileKey fileKey,
      final int offset,
      final int size) {
    Byte[] file = fileCache.getUnchecked(fileKey);

    byte[] message = new byte[size];
    for (int i = 0; i < size; i++) {
      message[i] = file[offset + i];
    }

    return message;
  }

  public void closeFile(final FileKey fileKey) {
    fileCache.invalidate(fileKey);
  }

  public void clearFileCache() {
    fileCache.invalidateAll();
  }

  private Byte[] newFile(final FileKey fileKey) {
    byte[] message =
      storageBasedFileAdapter.readFromFile(fileKey, 0, MAX_FILE_SIZE);
    Byte[] byteArr = new Byte[message.length];

    for (int i = 0; i < message.length; i++) {
      byteArr[i] = new Byte(message[i]);
    }

    return byteArr;
  }
}

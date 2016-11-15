package com.tinyfs.dao;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class FileAdapter {

  private static final int MAX_SYSTEM_SIZE = 5000;

  private LoadingCache<String, Byte[]> fileCache;

  public FileAdapter() {
    this.fileCache =
      CacheBuilder.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .<String, Byte[]> build(
          CacheLoader.from(this::newFile));
  }

  public void writeToFile(
      final String filename,
      final byte[] message,
      final int offset) {
    Byte[] file = fileCache.getUnchecked(filename);

    for (int i = 0; i < message.length; i++) {
      file[offset + i] = message[i];
    }
  }

  public byte[] readFromFile(
      final String filename,
      final int offset,
      final int size) {
    Byte[] file = fileCache.getUnchecked(filename);

    byte[] message = new byte[size];
    for (int i = 0; i < size; i++) {
      message[i] = file[offset + i];
    }

    return message;
  }

  private Byte[] newFile() {
    Byte[] file = new Byte[MAX_SYSTEM_SIZE];
    for (int i = 0; i < file.length; i++) {
      file[i] = 0;
    }
    return file;
  }
}

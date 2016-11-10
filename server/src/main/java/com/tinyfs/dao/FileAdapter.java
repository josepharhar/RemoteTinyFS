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

  private LoadingCache<String, Byte[]> fileSystemCache;

  public FileAdapter() {
    this.fileSystemCache =
      CacheBuilder.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(5, TimeUnit.MINUTES)
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

  private Byte[] newFile() {
    return
      IntStream.of(MAX_SYSTEM_SIZE)
        .mapToObj(i -> Byte.valueOf("0"))
        .collect(Collectors.toList())
        .toArray(new Byte[0]);
  }
}

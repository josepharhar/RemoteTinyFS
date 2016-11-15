package com.tinyfs.dao;

public interface FileAdapter {

  public static final int MAX_FILE_SIZE = 5000;

  public void writeToFile(
      final String fileSystemName,
      final byte[] message,
      final int offset);

  public byte[] readFromFile(
      final String fileSystemName,
      final int offset,
      final int size);
}
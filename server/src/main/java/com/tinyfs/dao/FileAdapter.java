package com.tinyfs.dao;

public interface FileAdapter {

  public static final int MAX_FILE_SIZE = 5000;

  public void writeToFile(
      final FileKey fileKey,
      final byte[] message,
      final int offset);

  public byte[] readFromFile(
      final FileKey fileKey,
      final int offset,
      final int size);
}

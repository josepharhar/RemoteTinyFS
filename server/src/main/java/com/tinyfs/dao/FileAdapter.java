package com.tinyfs.dao;

public interface FileAdapter {

  // BLOCKSIZE * MAX_SUPPORTED_BLOCKS from TinyFS
  public static final int MAX_FILE_SIZE = 256 * 256;

  public void writeToFile(
      final FileKey fileKey,
      final byte[] message,
      final int offset);

  public byte[] readFromFile(
      final FileKey fileKey,
      final int offset,
      final int size);
}

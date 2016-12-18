package com.tinyfs.dao;

public interface DiskAdapter {

  // BLOCKSIZE * MAX_SUPPORTED_BLOCKS from TinyFS
  public static final int MAX_DISK_SIZE = 256 * 256;

  public void writeToDisk(
      final DiskKey diskKey,
      final byte[] message,
      final int offset);

  public byte[] readFromDisk(
      final DiskKey diskKey,
      final int offset,
      final int size);

  public int getDiskSize(final DiskKey diskKey);
}

package com.tinyfs.dao;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
public class StorageBasedDiskAdapter implements DiskAdapter {

  private final Logger LOGGER = LogManager.getLogger(StorageBasedDiskAdapter.class);

  // Optional.empty means that the disk does not exist
  public Optional<Integer> getDiskSize(final DiskKey diskKey) {
    Path path = toDiskPath(diskKey);
    Files.createDirectories(path);

    if (!Files.exists(path)) {
      return Optional.empty();
    }

    return Optional.of(Files.size(path));
  }

  public byte[] readFromDisk(final DiskKey diskKey) {
    return Files.readAllBytes(toDiskPath(diskKey));
  }

  public void writeToDisk(
      final DiskKey diskKey,
      final byte[] data) {
    Files.write(toDiskPath(diskKey), data);
  }

  private Path toDiskPath(final DiskKey diskKey) {
    return Paths.get(
        DiskConstants.DISK_DIRECTORY,
        diskKey.getUsername(),
        diskKey.getDiskname() + "." + DiskConstants.FILE_EXTENSION);
  }
}

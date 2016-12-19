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

  @Override
  public void writeToDisk(
      final DiskKey diskKey,
      final byte[] message,
      final int offset) {
    File file = new File(toDiskLocation(diskKey));
    file.getParentFile().mkdirs();

    boolean newDisk = false;
    try {
      newDisk = file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    if (newDisk) {
      LOGGER.info("Created file: " + diskKey);
      writeToDisk(
        file,
        ByteBuffer
          .allocate(MAX_DISK_SIZE)
          .array(),
        0);
    }

    if (message != null) {
      writeToDisk(file, message, offset);
    }
  }

  @Override
  public byte[] readFromDisk(
      final DiskKey diskKey,
      final int offset,
      final int size) {

    File file = new File(toDiskLocation(diskKey));
    file.getParentFile().mkdirs();

    boolean newDisk = false;
    try {
      LOGGER.info("Created file: " + diskKey);
      newDisk = file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    if (newDisk) {
      byte[] message = 
        ByteBuffer
          .allocate(MAX_DISK_SIZE)
          .array();

      writeToDisk(
        file,
        message,
        0);

      return Arrays.copyOf(message, size);
    }

    try (RandomAccessFile raFile = new RandomAccessFile(file, "r")) {
      byte[] message =
        ByteBuffer
          .allocate(size)
          .array();

      raFile.seek(offset);
      raFile.read(message);

      return message;
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    return null;
  }

  @Override
  public Byte[] openDisk(
      final DiskKey diskKey,
      final int disksize) {

  }

  private void writeToDisk(
      final File file,
      final byte[] message,
      final int offset) {
    try (RandomAccessFile raFile = new RandomAccessFile(file, "rw")) {
      raFile.seek(offset);
      raFile.write(message);
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }
  }

  private Path toDiskPath(final DiskKey diskKey) {
    return Paths.get(
        DiskConstants.DISK_DIRECTORY,
        diskKey.getUsername(),
        diskKey.getDiskname() + "." + DiskConstants.FILE_EXTENSION);
  }
}

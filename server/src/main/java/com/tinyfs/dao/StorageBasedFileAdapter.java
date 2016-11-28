package com.tinyfs.dao;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
public class StorageBasedFileAdapter implements FileAdapter {

  @Override
  public void writeToFile(
      final String fileName,
      final byte[] message,
      final int offset) {
    File file = new File(fileName);

    boolean newFile = false;
    try {
      newFile = file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    if (newFile) {
      writeToFile(
        file,
        ByteBuffer
          .allocate(MAX_FILE_SIZE)
          .array(),
        0);
    }

    if (message != null) {
      writeToFile(file, message, offset);
    }
  }

  @Override
  public byte[] readFromFile(
      final String fileName,
      final int offset,
      final int size) {

    File file = new File(fileName);

    boolean newFile = false;
    try {
      newFile = file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    if (newFile) {
      byte[] message = 
        ByteBuffer
          .allocate(MAX_FILE_SIZE)
          .array();

      writeToFile(
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

  private void writeToFile(
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
}

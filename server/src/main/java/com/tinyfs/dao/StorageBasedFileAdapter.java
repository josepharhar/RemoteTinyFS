package com.tinyfs.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Files;

import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
public class StorageBasedFileAdapter implements FileAdapter {

  @Override
  public void writeToFile(
      final String fileSystemName,
      final byte[] message,
      final int offset) {
    File file = new File(fileSystemName);

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

    if (message == null) {
      writeToFile(file, message, offset);
    }
  }

  @Override
  public byte[] readFromFile(
      final String fileSystemName,
      final int offset,
      final int size) {

    File file = new File(fileSystemName);

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

      return message;
    }

    try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath())) {
      char[] message =
        CharBuffer
          .allocate(MAX_FILE_SIZE)
          .array();

      bufferedReader.read(message);

      return new String(message).getBytes();
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
    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath())) {
      bufferedWriter.write(
        toCharArray(message),
        offset,
        message.length / 2);
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }
  }

  private char[] toCharArray(final byte[] message) {
    return ByteBuffer.wrap(message)
      .asCharBuffer()
      .array();
  }
}

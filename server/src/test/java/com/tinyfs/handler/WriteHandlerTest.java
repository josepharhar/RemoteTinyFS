package com.tinyfs.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tinyfs.dao.FileKey;
import com.tinyfs.dao.HighlyAvailableFileAdapter;
import com.tinyfs.dao.HighlyAvailableRemovalListener;
import com.tinyfs.dao.StorageBasedFileAdapter;

public class WriteHandlerTest {
  @Test
  public void test() {
    StorageBasedFileAdapter adapter = new StorageBasedFileAdapter();

    String testPost = "Test post please ignore.";

    HighlyAvailableRemovalListener listener = new HighlyAvailableRemovalListener(adapter);
    HighlyAvailableFileAdapter cache = new HighlyAvailableFileAdapter(listener, adapter);

    cache.writeToFile(
      FileKey.builder()
        .username("avilan")
        .fileName("TEST")
        .build(),
      testPost.getBytes(),
      17);
    String readResult = new String(cache.readFromFile(
      FileKey.builder()
        .username("avilan")
        .fileName("TEST")
        .build(),
      17,
      testPost.length()));

    assertEquals(testPost, readResult);
    cache.clearFileCache();
    test2();
//    File file = new File("TEST");
//    file.delete();
  }

  public void test2() {
    StorageBasedFileAdapter adapter = new StorageBasedFileAdapter();

    String testPost = "Test post please ignore.";

    HighlyAvailableRemovalListener listener = new HighlyAvailableRemovalListener(adapter);
    HighlyAvailableFileAdapter cache = new HighlyAvailableFileAdapter(listener, adapter);

    String readResult = new String(cache.readFromFile(
      FileKey.builder()
        .username("avilan")
        .fileName("TEST")
        .build(),
      17,
      testPost.length()));

    assertEquals(testPost, readResult);
  }
}
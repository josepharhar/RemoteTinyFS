package com.tinyfs.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tinyfs.dao.DiskKey;
import com.tinyfs.dao.HighlyAvailableDiskAdapter;
import com.tinyfs.dao.HighlyAvailableRemovalListener;
import com.tinyfs.dao.StorageBasedDiskAdapter;

public class WriteHandlerTest {
  @Test
  public void test() {
    StorageBasedDiskAdapter adapter = new StorageBasedDiskAdapter();

    String testPost = "Test post please ignore.";

    HighlyAvailableRemovalListener listener = new HighlyAvailableRemovalListener(adapter);
    HighlyAvailableDiskAdapter cache = new HighlyAvailableDiskAdapter(listener, adapter);

    cache.writeToDisk(
      DiskKey.builder()
        .username("avilan")
        .diskname("TEST")
        .build(),
      testPost.getBytes(),
      17);
    String readResult = new String(cache.readFromDisk(
      DiskKey.builder()
        .username("avilan")
        .diskname("TEST")
        .build(),
      17,
      testPost.length()));

    assertEquals(testPost, readResult);
    cache.clearDiskCache();
    test2();
//    File file = new File("TEST");
//    file.delete();
  }

  public void test2() {
    StorageBasedDiskAdapter adapter = new StorageBasedDiskAdapter();

    String testPost = "Test post please ignore.";

    HighlyAvailableRemovalListener listener = new HighlyAvailableRemovalListener(adapter);
    HighlyAvailableDiskAdapter cache = new HighlyAvailableDiskAdapter(listener, adapter);

    String readResult = new String(cache.readFromDisk(
      DiskKey.builder()
        .username("avilan")
        .diskname("TEST")
        .build(),
      17,
      testPost.length()));

    assertEquals(testPost, readResult);
  }
}

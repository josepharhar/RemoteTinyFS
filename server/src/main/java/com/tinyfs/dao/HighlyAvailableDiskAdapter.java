package com.tinyfs.dao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class HighlyAvailableDiskAdapter implements DiskAdapter {

  @SuppressWarnings("unused")
  private final HighlyAvailableRemovalListener highlyAvailableRemovalListener;
  private final StorageBasedDiskAdapter storageBasedDiskAdapter;

  private LoadingCache<DiskKey, Byte[]> diskCache;

  public HighlyAvailableDiskAdapter(
      final HighlyAvailableRemovalListener highlyAvailableRemovalListener,
      final StorageBasedDiskAdapter storageBasedDiskAdapter) {
    this.highlyAvailableRemovalListener = highlyAvailableRemovalListener;
    this.storageBasedDiskAdapter = storageBasedDiskAdapter;

    this.diskCache =
      CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .removalListener(highlyAvailableRemovalListener)
        .<DiskKey, Byte[]> build(
          CacheLoader.from(this::newDisk));
  }

  @Override
  public void writeToDisk(
      final DiskKey diskKey,
      final byte[] message,
      final int offset) {
    try {
      Byte[] disk = diskCache.get(diskKey);

      for (int i = 0; i < message.length; i++) {
        disk[offset + i] = message[i];
      }
    } catch (ExecutionException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }
  }

  @Override
  public byte[] readFromDisk(
      final DiskKey diskKey,
      final int offset,
      final int size) {
    Byte[] disk = diskCache.getUnchecked(diskKey);

    byte[] message = new byte[size];
    for (int i = 0; i < size; i++) {
      message[i] = disk[offset + i];
    }

    return message;
  }

  @Override
  public int openDiskAndGetSize(
      final DiskKey diskKey,
      final int disksize) {
    Byte[] disk = diskCache.getUnchecked(diskKey);
    return disk.length();
  }

  public void closeDisk(final DiskKey diskKey) {
    diskCache.invalidate(diskKey);
  }

  public void clearDiskCache() {
    diskCache.invalidateAll();
  }

  private Byte[] newDisk(final DiskKey diskKey, final int disksize) {
    byte[] message =
      storageBasedDiskAdapter.readFromDisk(diskKey, 0, MAX_DISK_SIZE);
    Byte[] byteArr = new Byte[message.length];

    for (int i = 0; i < message.length; i++) {
      byteArr[i] = new Byte(message[i]);
    }

    return byteArr;
  }
}

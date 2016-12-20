package com.tinyfs.dao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class HighlyAvailableDiskAdapter {

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

  public void writeToDisk(
      final DiskKey diskKey,
      final byte[] message,
      final int offset) {
    Byte[] disk = diskCache.getUnchecked(diskKey);

    for (int i = 0; i < message.length; i++) {
      disk[offset + i] = message[i];
    }
  }

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

    // If the disk is in the cache, get it
    // else, get the disk from storage if it exists in storage (throw exception) and put into cache
    // then return byte[]
  }

  public int openDisk(
      final DiskKey diskKey,
      final int diskSize,
      final OpenMode openMode) {
    Byte[] disk = diskCache.getIfPresent(diskKey);
    if (disk != null) {
      return disk.length;
    }

    // disk isn't in cache. but is it in storage?
    Optional<Integer> existingDiskSize = storageBasedDiskAdapter.getDiskSize(diskKey);
    if (existingDiskSize.isEmpty()) {
      // disk does not exist.
      if (openMode == OpenMode.DONT_CREATE) {
        throw new DiskNotFoundException();
      }
      // disk needs to be created
      diskCache.put(diskKey, new Byte[diskSize]);
      return diskSize;
    }

    // disk is in storage, expand its size if needed
    // we could do this by loading it into the cache, then expanding its size
    if (existingDiskSize.get() < diskSize) {
      disk = diskCache.getUnchecked(diskKey);
      Byte[] newDisk = new Byte[diskSize];
      for (int i = 0; i < disk.length; i++) {
        newDisk[i] = disk[i];
      }
      for (int i = disk.length; i < newDisk.length; i++) {
        newDisk[i] = 0;
      }
      diskCache.put(diskKey, newDisk);
    }
  }

  public void closeDisk(final DiskKey diskKey) {
    diskCache.invalidate(diskKey);
  }

  public void clearDiskCache() {
    diskCache.invalidateAll();
  }

  private Byte[] newDisk(final DiskKey diskKey) {
    // A disk is not in the cache, and may or may not be on disk.
    Optional<Integer> diskSize = getDiskSize(diskKey);
    if (diskSize.isEmpty()) {
      throw new DiskNotFoundException();
    }

    byte[] message = storageBasedDiskAdapter.readFromDisk(diskKey);

    Byte[] byteArr = new Byte[message.length];
    for (int i = 0; i < message.length; i++) {
      byteArr[i] = new Byte(message[i]);
    }

    return byteArr;
  }
}

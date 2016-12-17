package com.tinyfs.dao;

import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableSet;

@Component
public class HighlyAvailableRemovalListener implements RemovalListener<DiskKey, Byte[]> {

  private final Logger LOGGER = LogManager.getLogger(HighlyAvailableRemovalListener.class);

  private static final Set<RemovalCause> ACCEPTABLE_CAUSES =
    ImmutableSet.of(
      RemovalCause.EXPIRED, RemovalCause.EXPLICIT, RemovalCause.SIZE);

  private final StorageBasedDiskAdapter storageBasedDiskAdapter;

  @Inject
  public HighlyAvailableRemovalListener(final StorageBasedDiskAdapter storageBasedDiskAdapter) {
    this.storageBasedDiskAdapter = storageBasedDiskAdapter;
  }

  @Override
  public void onRemoval(final RemovalNotification<DiskKey, Byte[]> notification) {
    if (!ACCEPTABLE_CAUSES.contains(notification.getCause())) {
      return;
    }

    LOGGER.info(notification.getKey() + " evicted, commiting to storage.");

    storageBasedDiskAdapter.writeToDisk(
      notification.getKey(),
      ArrayUtils.toPrimitive(notification.getValue()),
      0);
  }

}

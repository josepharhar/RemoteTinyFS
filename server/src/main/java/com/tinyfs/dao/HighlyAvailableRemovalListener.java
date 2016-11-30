package com.tinyfs.dao;

import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableSet;

@Component
public class HighlyAvailableRemovalListener implements RemovalListener<String, Byte[]> {

  private static final Set<RemovalCause> ACCEPTABLE_CAUSES =
    ImmutableSet.of(
      RemovalCause.EXPIRED, RemovalCause.EXPLICIT, RemovalCause.SIZE);

  private final StorageBasedFileAdapter storageBasedFileAdapter;

  @Inject
  public HighlyAvailableRemovalListener(final StorageBasedFileAdapter storageBasedFileAdapter) {
    this.storageBasedFileAdapter = storageBasedFileAdapter;
  }

  @Override
  public void onRemoval(final RemovalNotification<String, Byte[]> notification) {
    if (!ACCEPTABLE_CAUSES.contains(notification.getCause())) {
      return;
    }

    storageBasedFileAdapter.writeToFile(
      notification.getKey(),
      ArrayUtils.toPrimitive(notification.getValue()),
      0);
  }

}

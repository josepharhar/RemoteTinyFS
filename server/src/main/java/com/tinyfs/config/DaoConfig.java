package com.tinyfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyfs.dao.HighlyAvailableDiskAdapter;
import com.tinyfs.dao.HighlyAvailableRemovalListener;
import com.tinyfs.dao.StorageBasedDiskAdapter;

@Configuration
public class DaoConfig {
  @Bean(destroyMethod = "clearDiskCache")
  public HighlyAvailableDiskAdapter highlyAvailableDiskAdapter(
      final HighlyAvailableRemovalListener highlyAvailableRemovalListener,
      final StorageBasedDiskAdapter storageBasedDiskAdapter) {
    return new HighlyAvailableDiskAdapter(highlyAvailableRemovalListener, storageBasedDiskAdapter);
  }
}

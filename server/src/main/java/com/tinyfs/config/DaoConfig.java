package com.tinyfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyfs.dao.HighlyAvailableFileAdapter;
import com.tinyfs.dao.HighlyAvailableRemovalListener;
import com.tinyfs.dao.StorageBasedFileAdapter;

@Configuration
public class DaoConfig {
  @Bean
  public HighlyAvailableFileAdapter highlyAvailableFileAdapter(
      final HighlyAvailableRemovalListener highlyAvailableRemovalListener,
      final StorageBasedFileAdapter storageBasedFileAdapter) {
    return new HighlyAvailableFileAdapter(highlyAvailableRemovalListener, storageBasedFileAdapter);
  }
}
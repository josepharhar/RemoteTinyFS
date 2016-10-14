package com.tinyfs.credentials.cache;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

@Component
public class ClientCacheAdapter {
  private Cache<String, List<String>> clientCache;

  public ClientCacheAdapter() {
    this.clientCache =
      CacheBuilder.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .<String, List<String>> build();
  }

  public void registerClient(
      final String clientId,
      final List<String> fileSystemNames) {
    clientCache.put(clientId, fileSystemNames);
  }

  public List<String> getRegisteredFileSystems(
      final String clientId) {
    return Optional.ofNullable(clientCache.getIfPresent(clientId))
      .orElse(ImmutableList.of());
  }
}

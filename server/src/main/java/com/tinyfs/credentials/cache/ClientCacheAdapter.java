package com.tinyfs.credentials.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Component
public class ClientCacheAdapter {
  private Cache<String, String> clientCache;

  public ClientCacheAdapter() {
    this.clientCache =
      CacheBuilder.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .<String, String> build();
  }

  public void registerClient(
      final String clientId,
      final String username) {
    clientCache.put(clientId, username);
  }

  public String getRegisteredUsername(
      final String clientId) {
    return Optional.ofNullable(clientCache.getIfPresent(clientId))
      .orElse(StringUtils.EMPTY);
  }
}

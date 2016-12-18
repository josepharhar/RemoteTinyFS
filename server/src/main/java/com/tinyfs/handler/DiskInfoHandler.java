package com.tinyfs.handler;

import javax.inject.Inject;

@Component
public class DiskInfoHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final HighlyAvailableDiskAdapter diskAdapter;

  public DiskInfoHandler(
      ClientCacheAdapter clientCacheAdapter,
      HighlyAvailableDiskAdapter diskAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.diskAdapter = diskAdapter;
  }

  public DiskInfoResponse getDiskInfo(DiskInfoRequest request) {
    String sessionId = request.getSessionId();
    String username = clientCacheAdapter.getRegisteredUsername(sessionId);
    String diskname = request.getDiskname();

    if (StringUtils.isEmpty(diskname) || StringUtils.isEmpty(username)) {
      return DiskInfoResponse.newBuilder()
        .disksize(-1)
        .responseCode(ResponseCode.DISK_NOT_FOUND)
        .build();
    }

    return DiskInfoResponse.newBuilder()
      .build();
  }
}

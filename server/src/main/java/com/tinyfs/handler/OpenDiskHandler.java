package com.tinyfs.handler;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.tinyfs.credentials.cache.ClientCacheAdapter;
import com.tinyfs.dao.DiskKey;
import com.tinyfs.dao.HighlyAvailableDiskAdapter;
import com.tinyfs.exception.UnregisteredDiskException;
import com.tinyfs.model.ServiceModel.OpenDiskRequest;
import com.tinyfs.model.ServiceModel.OpenDiskResponse;

@Component
public class OpenDiskHandler {

  private final ClientCacheAdapter clientCacheAdapter;
  private final HighlyAvailableDiskAdapter diskAdapter;

  public OpenDiskHandler(
      ClientCacheAdapter clientCacheAdapter,
      HighlyAvailableDiskAdapter diskAdapter) {
    this.clientCacheAdapter = clientCacheAdapter;
    this.diskAdapter = diskAdapter;
  }

  public OpenDiskResponse openDisk(OpenDiskRequest request) {
    String sessionId = request.getSessionId();
    String username = clientCacheAdapter.getRegisteredUsername(sessionId);
    String diskname = request.getDiskname();

    if (StringUtils.isEmpty(diskname) || StringUtils.isEmpty(username)) {
      return OpenDiskResponse.newBuilder()
        .disksize(-1)
        .responseCode(ResponseCode.DISK_NOT_FOUND)
        .build();
    }

    try {
      int diskSize = diskAdapter.openDisk(
          diskKey,
          request.diskSize,
          request.openMode);
      return OpenDiskResponse.newBuilder()
        .diskSize(diskSize)
        .responseCode(ResponseCode.SUCCESS)
        .build();
    } catch (DiskNotFoundException e) {
      return OpenDiskResponse.newBuilder()
        .diskSize(-1)
        .responseCode(ResponseCode.DISK_NOT_FOUND)
        .build();
    }
  }
}

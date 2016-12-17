package com.tinyfs.handler;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.dao.DiskAdapter;
import com.tinyfs.dao.DiskKey;
import com.tinyfs.dao.HighlyAvailableDiskAdapter;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

@RestController
public class GetDiskHandler {

  private final DiskAdapter diskAdapter;
  private final ClientRegistrationRequestValidator registrationRequestValidator;

  @Inject
  public GetDiskHandler(
      HighlyAvailableDiskAdapter diskAdapter,
      ClientRegistrationRequestValidator registrationRequestValidator) {
    this.diskAdapter = diskAdapter;
    this.registrationRequestValidator = registrationRequestValidator;
  }

  @RequestMapping("/getDisk")
  public byte[] getDisk(
      @RequestParam(value="disk") String disk,
      @RequestParam(value="token") String token) {
    ClientCredentials credentials =
        registrationRequestValidator.toClientCredentials(token.getBytes());

    DiskKey diskKey = DiskKey.builder()
      .username(credentials.getUsername())
      .diskname(disk)
      .build();

    return diskAdapter.readFromDisk(diskKey, 0, DiskAdapter.MAX_DISK_SIZE);
  }
}

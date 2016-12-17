package com.tinyfs.handler;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.dao.FileAdapter;
import com.tinyfs.dao.FileKey;
import com.tinyfs.dao.HighlyAvailableFileAdapter;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

@RestController
public class DeleteDiskHandler {

  private final FileAdapter fileAdapter;
  private final ClientRegistrationRequestValidator registrationRequestValidator;

  @Inject
  public DeleteDiskHandler(
      HighlyAvailableFileAdapter fileAdapter,
      ClientRegistrationRequestValidator registrationRequestValidator) {
    this.fileAdapter = fileAdapter;
    this.registrationRequestValidator = registrationRequestValidator;
  }

  @RequestMapping("/deleteDisk")
  public void deleteDisk(
      @RequestParam(value="disk") String disk,
      @RequestParam(value="token") String token) {
    
    ClientCredentials credentials =
        registrationRequestValidator.toClientCredentials(token.getBytes());
    
    FileKey fileKey = FileKey.builder()
      .username(credentials.getUsername())
      .fileName(disk)
      .build();

    // TODO what if disk name is invalid?
    // TODO delete disk by removing from cache and deleting file
    //      also need to make sure that no websocket connections
    //      are using the disk, and that no other requests can be
    //      started for that user while deleting
  }
}

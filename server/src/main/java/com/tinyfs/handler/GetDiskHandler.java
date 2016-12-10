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
public class GetDiskHandler {

  private final FileAdapter fileAdapter;
  private final ClientRegistrationRequestValidator registrationRequestValidator;

  @Inject
  public GetDiskHandler(
      HighlyAvailableFileAdapter fileAdapter,
      ClientRegistrationRequestValidator registrationRequestValidator) {
    this.fileAdapter = fileAdapter;
    this.registrationRequestValidator = registrationRequestValidator;
  }

  @RequestMapping("/getDisk")
  public byte[] getDisk(
      @RequestParam(value="disk") String disk,
      @RequestParam(value="token") String token) {
    ClientCredentials credentials =
        registrationRequestValidator.toClientCredentials(token.getBytes());

    FileKey fileKey = FileKey.builder()
      .username(credentials.getUsername())
      .fileName(disk)
      .build();

    return fileAdapter.readFromFile(fileKey, 0, FileAdapter.MAX_FILE_SIZE);
  }
}

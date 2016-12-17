package com.tinyfs.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.tinyfs.auth.ClientCredentialsProto.ClientCredentials;
import com.tinyfs.dao.DiskConstants;
import com.tinyfs.validation.ClientRegistrationRequestValidator;

@RestController
public class ListDisksHandler {

  private final ObjectMapper mapper;
  private final ClientRegistrationRequestValidator registrationRequestValidator;

  @Inject
  public ListDisksHandler(
      ClientRegistrationRequestValidator registrationRequestValidator) {
    this.mapper = new ObjectMapper();
    this.registrationRequestValidator = registrationRequestValidator;
  }

  @RequestMapping("/listDisks")
  public String listDisks(
      @RequestParam("token") final String token) throws Exception {

    ClientCredentials credentials =
        registrationRequestValidator.toClientCredentials(token.getBytes());

    File diskDir = new File(DiskConstants.DISK_DIRECTORY);

    try {
      diskDir.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }

    return mapper.writeValueAsString(
      FileUtils.listFiles(
        new File(DiskConstants.DISK_DIRECTORY),
        new SuffixFileFilter(DiskConstants.FILE_EXTENSION),
        new NameFileFilter(credentials.getUsername()))
        .stream()
        .map(File::getName)
        .map(this::stripExtension)
        .collect(Collectors.toList()));
  }

  private String stripExtension(final String name) {
    return name.replace(".bin", "");
  }
}

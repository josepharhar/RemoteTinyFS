package com.tinyfs.handler;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.tinyfs.credentials.user.AllowableUsers;
import com.tinyfs.dao.FileConstants;

@RestController
public class ListFileHandler {

  private final ObjectMapper mapper = new ObjectMapper();

  @RequestMapping("/listFiles")
  public String listFiles(
      @RequestParam("username") final String username) throws Exception {

    File fileDir = new File(FileConstants.FILE_DIRECTORY);

    try {
      fileDir.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Throwables.propagate(e);
    } 

    return mapper.writeValueAsString(
      FileUtils.listFiles(
        new File(FileConstants.FILE_DIRECTORY),
        new SuffixFileFilter(FileConstants.FILE_EXTENSION),
        new NameFileFilter(username))
        .stream()
        .map(File::getName)
        .map(this::stripExtension)
        .collect(Collectors.toList()));
  }

  private String stripExtension(final String name) {
    return name.replace(".bin", "");
  }
}

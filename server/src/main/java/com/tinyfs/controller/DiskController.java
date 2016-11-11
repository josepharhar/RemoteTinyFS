package com.tinyfs.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tinyfs.dao.FileSystemAdapter;

@RestController
public class DiskController {

  private final FileSystemAdapter fileSystemAdapter;

  @Inject
  public DiskController(FileSystemAdapter fileSystemAdapter) {
    this.fileSystemAdapter = fileSystemAdapter;
  }

  @RequestMapping("/disk")
  public String getDisk(
      @RequestParam(value="disk") String disk,
      @RequestParam(value="token") String token) {
    System.out.println("DiskController::getDisk disk \"" + disk + "\", token \"" + token + "\"");
    //return "TODO";
    
    byte[] blocks = fileSystemAdapter.readFromFileSystem(disk, 0, 30 * 30);
    try {
      return new String(blocks, "UTF-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
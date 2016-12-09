package com.tinyfs.exception;

public class CredentialsException extends TinyFSException {

  private static final long serialVersionUID = 1L;

  public CredentialsException(String message) {
    super(message);
  }

  public CredentialsException(Exception e) {
    super(e);
  }

  public CredentialsException() {
  }

}

package com.tinyfs.exception;

public class InvalidArgumentException extends TinyFSException {
  private static final long serialVersionUID = 1L;

  public InvalidArgumentException() {
    super();
  }

  public InvalidArgumentException(final String message) {
    super(message);
  }
}
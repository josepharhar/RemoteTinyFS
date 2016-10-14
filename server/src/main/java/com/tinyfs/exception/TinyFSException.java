package com.tinyfs.exception;

public abstract class TinyFSException extends Exception {

  private static final long serialVersionUID = 1L;

  public TinyFSException() {
    super();
  }

  public TinyFSException(final String message) {
    super(message);
  }

  public TinyFSException(final Exception e) {
    super(e);
  }
}

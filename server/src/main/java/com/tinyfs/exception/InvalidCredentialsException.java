package com.tinyfs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCredentialsException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InvalidCredentialsException(Throwable e) {
    super(e);
  }

  public InvalidCredentialsException(String message, Throwable e) {
    super(message, e);
  }
}

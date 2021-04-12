package com.sss.scheduler.lock;

public class LockNotAquiredException extends RuntimeException {

  public LockNotAquiredException(String name, String lockedBy) {
    super(String.format("Unable to acquire lock %s for %s", name, lockedBy));
  }
}

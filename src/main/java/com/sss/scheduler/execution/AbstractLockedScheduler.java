package com.sss.scheduler.execution;

import com.sss.scheduler.lock.LockManager;

import javax.annotation.Resource;
import java.time.Duration;

public abstract class AbstractLockedScheduler {

  private final String lockName;

  private final Long lockDurationSeconds;

  @Resource
  private LockManager lockManager;

  protected AbstractLockedScheduler(String lockName, Long lockDurationSeconds) {
    this.lockName = lockName;
    this.lockDurationSeconds = lockDurationSeconds;
  }

  protected void executeWithLock() {
    boolean lock = lockManager.lock(lockName, Duration.ofSeconds(lockDurationSeconds));
    if (lock) {
      try {
        execute0();
      } finally {
        lockManager.unlock(lockName);
      }
    }
  }

  protected abstract void execute0();
}

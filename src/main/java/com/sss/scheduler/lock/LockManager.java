package com.sss.scheduler.lock;

import com.sss.scheduler.config.LockConfiguration;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

@Service
public class LockManager {

  private final String hostname = initHostname();

  @Resource
  private LockConfiguration lockConfiguration;

  @Resource
  private LockJdbcConnector lockJdbcConnector;

  public boolean lock(String name, Duration duration) {
    return lock(name, hostname, duration);
  }

  public boolean lock(String name, String lockedBy, Duration duration) {
    for(int i = 0; i < lockConfiguration.getRetries(); i++) {
      if(acquireLock(name, lockedBy, duration)) {
        return true;
      }
      try {
        Thread.sleep(lockConfiguration.getIntervalMs());
      } catch (InterruptedException e) {
        // nothing to do
      }
    }
    return false;
  }

  private boolean acquireLock(String name, String lockedBy, Duration duration) {
    try {
      lockJdbcConnector.cleanupOutdatedLock();
      lockJdbcConnector.lockOrExtendLock(name, lockedBy, duration);
      return true;
    } catch (PersistenceException | LockNotAquiredException e) {
      if(e.getMessage().contains("ConstraintViolationException")) {
        return false;
      }
      throw new IllegalStateException("Error while locking lock. Reason: " + e.getMessage(), e);
    }
  }

  public void unlock(String name) {
    unlock(name, hostname);
  }

  public void unlock(String name, String lockedBy) {
    lockJdbcConnector.unlock(name, lockedBy);
  }

  public boolean lockedAcquired(String name) {
    return lockedAcquired(name, hostname);
  }

  public boolean lockedAcquired(String name, String lockedBy) {
    return lockJdbcConnector.lockedAcquired(name, lockedBy);
  }

  public String getDefaultLockName() {
    return hostname;
  }

  @NonNull
  private static String initHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return UUID.randomUUID().toString();
    }
  }
}

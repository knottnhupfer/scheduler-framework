package com.sss.scheduler.lock;

import com.sss.scheduler.domain.SimpleLock;
import com.sss.scheduler.repository.SimpleLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class LockJdbcConnector {

  @PersistenceContext
  private EntityManager entityManager;

  @Resource
  private SimpleLockRepository simpleLockRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void cleanupOutdatedLock() {
    int removedOutdatedLocks = simpleLockRepository.deleteOutdatedLocks(Instant.now());
    if(removedOutdatedLocks > 0) {
      log.info("Removed {} outdated locks.", removedOutdatedLocks);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void lockOrExtendLock(String name, String lockedBy, Duration duration) {
    Instant now = Instant.now();
    SimpleLock activeLock = simpleLockRepository.findActiveLock(name, lockedBy, now);
    Instant lockedUntil = now.plus(duration);
    if(activeLock != null) {
      if(simpleLockRepository.extendLockLifetime(name, lockedBy, lockedUntil)) {
        log.warn("Updated active lock: {}", activeLock);
      }
      throw new LockNotAquiredException(name, lockedBy);
    } else {
      entityManager.createNativeQuery("INSERT INTO locks (name, locked_at, locked_by, locked_until) VALUES (?,?,?,?)")
              .setParameter(1, name)
              .setParameter(2, Instant.now())
              .setParameter(3, lockedBy)
              .setParameter(4, lockedUntil)
              .executeUpdate();
      log.info("Created new lock; name:{}, lockedBy:{}, lockedUntil:{}", name, lockedBy, lockedUntil);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void unlock(String name, String lockedBy) {
    log.info("Unlock {} lockedBy {}", name, lockedBy);
    simpleLockRepository.deleteAllByNameAndLockedBy(name, lockedBy);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean lockedAcquired(String name, String lockedBy) {
    return simpleLockRepository.findByNameAndLockedByAndLockedUntilIsBefore(name, lockedBy, Instant.now()) != null;
  }
}

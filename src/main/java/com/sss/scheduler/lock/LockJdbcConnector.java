package com.sss.scheduler.lock;

import com.sss.scheduler.domain.SimpleLock;
import com.sss.scheduler.repository.SimpleLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
class LockJdbcConnector {

  @PersistenceContext
  private EntityManager entityManager;

  @Resource
  private SimpleLockRepository simpleLockRepository;

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  public void lock(String name, String lockedBy, Duration duration) {

    Instant now = Instant.now();
    simpleLockRepository.deleteOutdatedLocks(now);
    SimpleLock activeLock = simpleLockRepository.findActiveLock(name, lockedBy, now);
    Instant lockedUntil = Instant.now().plus(duration);
    if(activeLock != null) {
      activeLock.setLockedUntil(lockedUntil);
      simpleLockRepository.save(activeLock);
      log.warn("Updated active lock: {}", activeLock);
    } else {
      entityManager.createNativeQuery("INSERT INTO locks (name, locked_at, locked_by, locked_until) VALUES (?,?,?,?)")
              .setParameter(1, name)
              .setParameter(2, Instant.now())
              .setParameter(3, lockedBy)
              .setParameter(4, lockedUntil)
              .executeUpdate();
      log.warn("Created new lock; name:{}, lockedBy:{}, lockedUntil:{}", name, lockedBy, lockedUntil);
    }
  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  public void unlock(String name, String lockedBy) {
    simpleLockRepository.deleteAllByNameAndLockedBy(name, lockedBy);
  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  public boolean lockedAcquired(String name, String lockedBy) {
    return false;
  }
}

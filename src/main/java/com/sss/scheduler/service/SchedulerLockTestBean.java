package com.sss.scheduler.service;

import com.sss.scheduler.lock.LockManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;

@Slf4j
@Component
public class SchedulerLockTestBean {

  public static String LOCK_NAME = "TEST-LOCK";

  @Resource
  private LockManager lockManager;

  @PostConstruct
  void scheduled() {

//    boolean lockAcquired = lockManager.lock(LOCK_NAME, Duration.ofMillis(3500));
//    Assert.isTrue(lockAcquired, "lock not acquired");
//
//    lockAcquired = lockManager.lock(LOCK_NAME, "second-node", Duration.ofMillis(1000));
//    Assert.isTrue(!lockAcquired, "lock has been acquired by another node");

//    lock = lockProvider.lock(new LockConfiguration("testing", Instant.now().plusSeconds(15)));
//    log.info("Shedlock acquired #2: {}", lock.isPresent());
//    lock.get().unlock();
//    log.info("Shedlock acquired #2: {}", lock.isPresent());
  }
}

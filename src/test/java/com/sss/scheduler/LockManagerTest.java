package com.sss.scheduler;

import com.sss.scheduler.lock.LockManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;

@SpringBootTest
public class LockManagerTest {

  public static String LOCK_NAME = "TEST-LOCK";

  @Resource
  private LockManager lockManager;

  @Test
  void lockManagerTest() {
    boolean lockAcquired = lockManager.lock(LOCK_NAME, Duration.ofSeconds(5));
    Assert.isTrue(lockAcquired, "lock not acquired");

    lockAcquired = lockManager.lock(LOCK_NAME, "second-node", Duration.ofSeconds(1));
    Assert.isTrue(!lockAcquired, "lock has been acquired by another node");

    lockManager.unlock(LOCK_NAME);
    lockAcquired = lockManager.lock(LOCK_NAME, "second-node", Duration.ofSeconds(1));
    Assert.isTrue(lockAcquired, "lock has been acquired by another node after unlock");
  }
}

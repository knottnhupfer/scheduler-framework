package com.sss.scheduler.execution;

import com.sss.scheduler.lock.LockManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.UUID;

@SpringBootTest
public class LockManagerTests {

  @Resource
  private LockManager lockManager;

  @Test
  void lockManagerTest() {
    String lockName = "lock-" + UUID.randomUUID().toString().substring(0,6);
    boolean lockAcquired = lockManager.lock(lockName, Duration.ofSeconds(4));
    Assert.isTrue(lockAcquired, "lock not acquired");

    lockAcquired = lockManager.lock(lockName, "second-node", Duration.ofSeconds(1));
    Assert.isTrue(!lockAcquired, "lock has been acquired by another node");

    lockManager.unlock(lockName);
    lockAcquired = lockManager.lock(lockName, "second-node", Duration.ofSeconds(4));
    Assert.isTrue(lockAcquired, "lock has been acquired by another node after unlock");

    lockAcquired = lockManager.lock(lockName, "third-node", Duration.ofSeconds(1));
    Assert.isTrue(!lockAcquired, "lock has been acquired by another node after lock");
  }

  @Test
  void verifyLockTimeoutTest() {
    String lockName = "lock-" + UUID.randomUUID().toString().substring(0,6);
    boolean lockAcquired = lockManager.lock(lockName, Duration.ofSeconds(1));
    Assert.isTrue(lockAcquired, "lock not acquired");

    lockAcquired = lockManager.lock(lockName, "second-node", Duration.ofSeconds(1));
    Assert.isTrue(lockAcquired, "lock has been acquired by another node");
  }
}

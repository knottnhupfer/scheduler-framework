package com.sss.scheduler.utils;

import org.junit.Assert;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

public final class CompareUtil {

  private CompareUtil() {
  }

  public static final void assertDateCloseToNow(OffsetDateTime now) {
    assertDateCloseToNow(now, Duration.ZERO);
  }

  public static final void assertDateCloseToNow(OffsetDateTime now, Duration duration) {
    Instant from = Instant.now().minusMillis(500);
    Instant to = Instant.now().plusMillis(500);
    Instant expectedTimestamp = now.toInstant().minus(duration);
    boolean valid = from.isBefore(expectedTimestamp) && to.isAfter(expectedTimestamp);
    if(!valid) {
      System.out.println("from: " + from);
      System.out.println("to: " + to);
      System.out.println("expected: " + expectedTimestamp);
    }
    Assert.assertTrue("now is not inside +-500 ms", valid);
  }
}

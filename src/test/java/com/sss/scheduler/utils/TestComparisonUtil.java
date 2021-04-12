package com.sss.scheduler.utils;

import org.junit.Assert;

import java.time.Duration;
import java.time.Instant;

public final class TestComparisonUtil {

  private static final long DELTA_MILLIS = 500;

  private TestComparisonUtil() {
  }

  public static final void assertDateCloseToNow(Instant now) {
    assertDateCloseToNow(now, Duration.ZERO);
  }

  public static final void assertDateCloseToNow(Instant executionDate, Duration duration) {
    Instant expectedDate = Instant.now().plus(duration);
    Instant from = expectedDate.minusMillis(DELTA_MILLIS);
    Instant to = expectedDate.plusMillis(DELTA_MILLIS);
    boolean valid = from.isBefore(executionDate) && to.isAfter(executionDate);
    if(!valid) {
      System.out.println("now: " + Instant.now());
      System.out.println("from: " + from);
      System.out.println("to: " + to);
      System.out.println("executionDate: " + executionDate);
    }
    Assert.assertTrue("now is not inside +-" + DELTA_MILLIS + " ms", valid);
  }
}

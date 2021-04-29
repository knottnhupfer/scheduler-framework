package com.sss.scheduler.job;

import com.sss.scheduler.tests.IncreaseByOneJob;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class JobTest {

  @Test
  public void testingCamelCaseJobName() {
    IncreaseByOneJob job = new IncreaseByOneJob();
    Assert.assertEquals("increaseByOneJob", job.getName());
  }
}

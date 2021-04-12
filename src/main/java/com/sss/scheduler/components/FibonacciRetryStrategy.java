package com.sss.scheduler.components;

import com.sss.scheduler.domain.JobInstance;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;

@Component
public class FibonacciRetryStrategy extends AbstractRetryStrategy {

  public FibonacciRetryStrategy() {
    super("FibonacciRetry");
  }

  @Override
  public Instant calculateNextExecution(JobInstance job, long executionDelay, long executedRetries) {
    Assert.notNull(job.getCreationDate(), "creation date is not set");
    Assert.isTrue(executionDelay > -1, "execution delay must be >= 0");
    Assert.isTrue(executedRetries > -1, "execution retries must be >= 0");
    return job.getCreationDate().plusSeconds(calculateIntervalsAmount(executedRetries) * executionDelay);
  }

  private static long calculateIntervalsAmount(long executedRetries) {
    if (executedRetries == 0) {
      return 1;
    } else if (executedRetries == 1) {
      return 2;
    }
    long n1 = 0, n2 = 1, n3 = 0, i, count = executedRetries + 3;

    for (i = 2; i < count; ++i) {
      n3 = n1 + n2;
      n1 = n2;
      n2 = n3;
    }
    return n3;
  }
}

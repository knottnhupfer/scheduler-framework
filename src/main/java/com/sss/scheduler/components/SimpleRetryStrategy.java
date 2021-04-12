package com.sss.scheduler.components;

import com.sss.scheduler.domain.JobInstance;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;

@Component
public class SimpleRetryStrategy extends AbstractRetryStrategy {

  public SimpleRetryStrategy() {
    super("SimpleRetry");
  }

  @Override
  public Instant calculateNextExecution(JobInstance job, long executionDelay, long executedRetries) {
    Assert.notNull(job.getCreationDate(), "creation date is not set");
    return job.getCreationDate().plusSeconds(executionDelay * (executedRetries + 1));
  }
}

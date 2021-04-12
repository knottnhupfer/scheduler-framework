package com.sss.scheduler.components;

import com.sss.scheduler.domain.Command;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Instant;

@Component
public class SimpleRetryStrategy extends AbstractRetryStrategy {

  public SimpleRetryStrategy() {
    super("SimpleRetry");
  }

  @Override
  public Instant calculateNextExecution(Command cmd, long executionDelay, long executedRetries) {
    Assert.notNull(cmd.getCreationDate(), "creation date is not set");
    return cmd.getCreationDate().toInstant().plusSeconds(executionDelay * executedRetries);
  }
}

package com.sss.scheduler.components;

import com.sss.scheduler.domain.Command;

import java.time.Instant;

public interface RetryStrategy {

  String getStrategyName();

  Instant calculateNextExecution(Command cmd, long executionDelay, long executedRetries);
}

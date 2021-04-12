package com.sss.scheduler.components;

import com.sss.scheduler.domain.JobInstance;

import java.time.Instant;

public interface RetryStrategy {

  String getStrategyName();

  Instant calculateNextExecution(JobInstance job, long executionDelay, long executedRetries);
}

package com.sss.scheduler.components;

import lombok.Data;

@Data
public class ExecutionConfiguration {

  private long retries;
  private long intervalSeconds;
  private String executionGroup;
  private RetryStrategy retryStrategy;
}

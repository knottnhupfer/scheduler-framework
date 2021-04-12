package com.sss.scheduler.config;

import lombok.Data;

import java.util.List;

@Data
public class JobsSchedulerConfiguration {

  private String name;
  private List<String> jobNames;
  private Long retries;
  private Long intervalSeconds;
  private String executionGroup;
  private String executionStrategy;
}

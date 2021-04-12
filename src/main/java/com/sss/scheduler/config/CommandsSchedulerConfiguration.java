package com.sss.scheduler.config;

import lombok.Data;

import java.util.List;

@Data
public class CommandsSchedulerConfiguration {

  private String name;
  private List<String> commandNames;
  private Long retries;
  private Long intervalSeconds;
  private String executionGroup;
  private String executionStrategy;
}

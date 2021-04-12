package com.sss.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "service.command-execution")
public class SchedulerConfiguration {

  private Long retries;
  private Long intervalSeconds;
  private String executionGroup;
  private String executionStrategy;
  private List<CommandsSchedulerConfiguration> configurations;
}

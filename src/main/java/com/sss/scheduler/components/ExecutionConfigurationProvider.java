package com.sss.scheduler.components;

import com.sss.scheduler.config.CommandsSchedulerConfiguration;
import com.sss.scheduler.config.SchedulerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class ExecutionConfigurationProvider {

  @Resource
  private List<RetryStrategy> retryStrategies;

  @Resource
  private SchedulerConfiguration schedulerConfiguration;

  private ExecutionConfiguration executionConfiguration;

  private Map<String, ExecutionConfiguration> executionConfigurations = new HashMap<>();

  @PostConstruct
  void initializeExecutionConfigurations() {
    createDefaultExecutionConfiguration();
    createSpecificCommandExecutionConfigurations();
  }

  public ExecutionConfiguration getExecutionConfigurationForCommand(String commandName) {
    if(executionConfigurations.containsKey(commandName)) {
      return executionConfigurations.get(commandName);
    }
    return executionConfiguration;
  }

  private void createDefaultExecutionConfiguration() {
    executionConfiguration = new ExecutionConfiguration();
    executionConfiguration.setRetries(schedulerConfiguration.getRetries());
    executionConfiguration.setIntervalSeconds(schedulerConfiguration.getIntervalSeconds());
    executionConfiguration.setExecutionGroup(schedulerConfiguration.getExecutionGroup());
    executionConfiguration.setRetryStrategy(getRetryStrategyByName(schedulerConfiguration.getExecutionStrategy()));
    log.info("Registered default command execution: {}", executionConfiguration);
  }

  private void createSpecificCommandExecutionConfigurations() {
    List<CommandsSchedulerConfiguration> configurations = schedulerConfiguration.getConfigurations();
    configurations.forEach(config -> {
      ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
      executionConfiguration.setRetries(config.getRetries());
      executionConfiguration.setIntervalSeconds(config.getIntervalSeconds());
      executionConfiguration.setExecutionGroup(config.getExecutionGroup());
      executionConfiguration.setRetryStrategy(getRetryStrategyByName(config.getExecutionStrategy()));
      config.getCommandNames().forEach(commandName -> {
        executionConfigurations.put(commandName, executionConfiguration);
      });
      log.info("Registered commands execution: {}", config);
    });
  }

  private RetryStrategy getRetryStrategyByName(String strategyName) {
    Assert.hasText(strategyName, "strategyName not set");
    Optional<RetryStrategy> retryStrategy = retryStrategies.stream()
            .filter(strategy -> strategy.getStrategyName().equalsIgnoreCase(strategyName)).findAny();
    if(!retryStrategy.isPresent()) {
      log.error("Unable to find retry strategy by name: {}", strategyName);
      throw new RuntimeException("Unable to find retry strategy by name: " + strategyName);
    }
    return retryStrategy.get();
  }
}

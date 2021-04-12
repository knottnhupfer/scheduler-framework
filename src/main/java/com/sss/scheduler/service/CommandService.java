package com.sss.scheduler.service;

import com.sss.scheduler.components.CommandExecutionParams;
import com.sss.scheduler.components.ExecutionConfiguration;
import com.sss.scheduler.components.ExecutionConfigurationProvider;
import com.sss.scheduler.components.RetryStrategy;
import com.sss.scheduler.domain.Command;
import com.sss.scheduler.domain.CommandMap;
import com.sss.scheduler.domain.CommandStatus;
import com.sss.scheduler.repository.CommandRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class CommandService {

  private static final int MAX_PRIORITY_AND_DEFAULT = 100;

  @Resource
  private CommandRepository commandRepository;

  @Resource
  private ExecutionConfigurationProvider executionConfigurationProvider;

  public void createCommand(Command cmd) {
    Assert.hasText(cmd.getCommandName(), "command name not set");
    if(cmd.getCommandMap() == null) {
      cmd.setCommandMap(new CommandMap());
    }
    cmd.setStatus(CommandStatus.OPEN);
    if(cmd.getPriority() == null || cmd.getPriority() > MAX_PRIORITY_AND_DEFAULT) {
      cmd.setPriority(MAX_PRIORITY_AND_DEFAULT);
    }
    updateExecutionParameters(cmd);
    commandRepository.save(cmd);
  }

  private void updateExecutionParameters(Command cmd) {
    ExecutionConfiguration executionConfiguration = executionConfigurationProvider.getExecutionConfigurationForCommand(cmd.getCommandName());
    Long executedRetries = cmd.getCommandMap().getLongValue("executedRetries", 0L);
    RetryStrategy retryStrategy = executionConfiguration.getRetryStrategy();
    Instant nextExecution = retryStrategy.calculateNextExecution(cmd, executionConfiguration.getIntervalSeconds(), executedRetries);
    cmd.setNextExecutionDate(nextExecution.atOffset(ZoneOffset.UTC));
  }
}

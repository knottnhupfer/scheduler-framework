package com.sss.scheduler;

import com.sss.scheduler.domain.Command;
import com.sss.scheduler.domain.CommandMap;
import com.sss.scheduler.domain.CommandStatus;
import com.sss.scheduler.service.CommandService;
import com.sss.scheduler.service.CommandTestService;
import com.sss.scheduler.utils.CompareUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.UUID;

@SpringBootTest
class CommandExecutionStrategyTests {

  private static String KEY_RETRIES = "executedRetries";

  @Resource
  private CommandService commandService;

  @Resource
  private CommandTestService commandTestService;

  @Test
  void fibonacciCalculationFirstRetry() {
	  String commandName = "ui-notification";
    createCommandsWithNameAndRetries(commandName, 1);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(60));
  }

  @Test
  void fibonacciCalculationSecondRetry() {
    String commandName = "ui-notification";
    createCommandsWithNameAndRetries(commandName, 2);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(90));
  }

  @Test
  void fibonacciCalculationThirdRetry() {
    String commandName = "ui-notification";
    createCommandsWithNameAndRetries(commandName, 3);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(150));
  }

  @Test
  void fibonacciCalculationFourthRetry() {
    String commandName = "ui-notification";
    createCommandsWithNameAndRetries(commandName, 4);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(240));
  }

  @Test
  void fibonacciCalculationFifthRetry() {
    String commandName = "ui-notification";
    createCommandsWithNameAndRetries(commandName, 5);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(390));
  }

  @Test
  void simpleCalculationFirstRetry() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    createCommandsWithNameAndRetries(commandName, 1);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(60));
  }

  @Test
  void simpleCalculationSecondRetry() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    createCommandsWithNameAndRetries(commandName, 2);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(120));
  }

  @Test
  void simpleCalculationThirdRetry() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    createCommandsWithNameAndRetries(commandName, 3);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(180));
  }

  @Test
  void simpleCalculationFourthRetry() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    createCommandsWithNameAndRetries(commandName, 4);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(240));
  }

  @Test
  void simpleCalculationFifthRetry() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    createCommandsWithNameAndRetries(commandName, 5);
    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(300));
  }

  private void createCommandsWithNameAndRetries(String commandName, long retries) {
    Command command = new Command();
    command.setCommandName(commandName);
    CommandMap commandMap = new CommandMap();
    commandMap.putLongValue(KEY_RETRIES, retries);
    command.setCommandMap(commandMap);
    commandService.createCommand(command);
  }
}

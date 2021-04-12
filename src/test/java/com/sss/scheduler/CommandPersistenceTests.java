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
class CommandPersistenceTests {

  @Resource
  private CommandService commandService;

  @Resource
  private CommandTestService commandTestService;

	@Test
	void storeSuccessfullyCommand() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    Command command = new Command();
    command.setCommandName(commandName);
    command.setPriority(10);

    CommandMap commandMap = new CommandMap();
    commandMap.putValue("commandName", commandName);
    command.setCommandMap(commandMap);
    commandService.createCommand(command);

    Command fetchedCommand = commandTestService.getCommandByName(commandName);

    Assert.assertNotNull(fetchedCommand.getId());
    Assert.assertEquals(fetchedCommand.getStatus(), CommandStatus.OPEN);
    Assert.assertEquals(fetchedCommand.getCommandName(), commandName);
    Assert.assertEquals(fetchedCommand.getPriority().longValue(), 10L);
    Assert.assertEquals(fetchedCommand.getCommandMap().getStringValue("commandName"), commandName);
    CompareUtil.assertDateCloseToNow(fetchedCommand.getCreationDate());
    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(60));
  }

  @Test
  void storeCommandAndLoadCommandConfiguration() {
    String commandName = "ui-notification";
    Command command = new Command();
    command.setCommandName(commandName);
    commandService.createCommand(command);

    Command fetchedCommand = commandTestService.getCommandByName(commandName);
    Assert.assertEquals(fetchedCommand.getPriority().longValue(), 100L);
    CompareUtil.assertDateCloseToNow(fetchedCommand.getNextExecutionDate(), Duration.ofSeconds(30));
  }

  @Test
  void storeCommandAndUpdatePriority() {
    String commandName = "testCommand-" + UUID.randomUUID().toString().substring(0, 4);
    Command command = new Command();
    command.setCommandName(commandName);
    command.setPriority(1207);
    commandService.createCommand(command);

    Command fetchedCommand = commandTestService.getCommandByName(commandName);
    Assert.assertEquals(fetchedCommand.getPriority().longValue(), 100L);
  }
}

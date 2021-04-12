package com.sss.scheduler.service;

import com.sss.scheduler.domain.Command;
import com.sss.scheduler.repository.CommandRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CommandTestService {

  @Resource
  private CommandRepository commandRepository;

  public Command getCommandByName(String commandName) {
    return commandRepository.findAllCommandsByName(commandName).stream().findFirst().get();
  }
}

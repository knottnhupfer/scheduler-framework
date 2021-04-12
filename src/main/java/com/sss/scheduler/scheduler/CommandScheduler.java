package com.sss.scheduler.scheduler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CommandScheduler {

  private boolean applicationActive = false;

  @EventListener
  void enableExecuter(ContextStartedEvent event) {
    applicationActive = true;
  }

  @Scheduled(fixedRateString = "${service.command-execution.command-fetching-interval}")
  void executeCommands() {
    if(!applicationActive) {
      return;
    }
    System.out.println("Retrieve commands to execute.");
    // retrieve and prepare all commands to execute
    // start all commands
  }
}



package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

@Component
public class SuccessfulJob implements Job {

  @Override
  public void execute(ExecutionMap map) {

  }
}

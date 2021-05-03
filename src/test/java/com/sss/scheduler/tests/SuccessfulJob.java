package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

@Component(SuccessfulJob.NAME)
public class SuccessfulJob implements Job {

  public static final String NAME = "successfulJob";

  @Override
  public void execute(ExecutionMap map) {

  }
}

package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

@Component("failingJob")
public class ErrornousJob implements Job {

  @Override
  public void execute(ExecutionMap map) {
    throw new IllegalStateException("Execute failing job.");
  }
}

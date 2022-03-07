package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

@Component(ErrornousJob.NAME)
public class ErrornousJob implements Job {

  public static final String NAME = "failingJob";

  @Override
  public void execute(ExecutionMap map) {
    StringBuffer buffer = new StringBuffer();
    for(int i = 0; i < 120; i++) {
      buffer.append("Execute failing job. -> ");
    }
    throw new IllegalStateException(buffer.toString());
  }
}

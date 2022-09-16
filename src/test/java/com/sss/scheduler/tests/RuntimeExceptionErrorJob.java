package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import com.sss.scheduler.execution.JobFailedStatus;
import org.springframework.stereotype.Component;

@Component(RuntimeExceptionErrorJob.NAME)
public class RuntimeExceptionErrorJob implements Job {

  public static final String NAME = "runtimeExceptionErrorJob";

  @Override
  public void execute(ExecutionMap map) {
    throw new RuntimeException("Runtime exception error happened.");
  }

  public void executeJobFailed(Long businessObjectId, ExecutionMap map, JobFailedStatus failedStatus) {
    System.out.println(String.format("Job failed with businessObjectId:%d and status: %s", businessObjectId, failedStatus));
  }
}

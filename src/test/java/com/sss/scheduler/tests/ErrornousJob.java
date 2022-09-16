package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import com.sss.scheduler.execution.JobFailedStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
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

  public void executeJobFailed(Long businessObjectId, ExecutionMap map, JobFailedStatus failedStatus) {
    log.info("Job failed with businessObjectId:{} and status: {}", businessObjectId, failedStatus);
  }
}

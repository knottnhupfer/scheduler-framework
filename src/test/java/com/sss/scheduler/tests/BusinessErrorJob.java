package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.BusinessException;
import com.sss.scheduler.execution.Job;
import com.sss.scheduler.execution.JobFailedStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component(BusinessErrorJob.NAME)
public class BusinessErrorJob implements Job {

  public static final String NAME = "businessErrorJob";

  @Override
  public void execute(ExecutionMap map) {
    throw new BusinessException("Business error happened.");
  }

  public void executeJobFailed(Long businessObjectId, ExecutionMap map, JobFailedStatus failedStatus) {
    log.info("Job failed with businessObjectId:{} and status: {}", businessObjectId, failedStatus);
  }
}

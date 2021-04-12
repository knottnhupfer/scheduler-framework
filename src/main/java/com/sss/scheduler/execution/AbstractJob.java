package com.sss.scheduler.execution;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.domain.JobInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class AbstractJob {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void runJob(JobInstance jobInstance) {
    try {

      execute(jobInstance.getJobMap());
    } catch (Exception e) {

    }
  }

  protected abstract void execute(ExecutionMap map);
}

package com.sss.scheduler.execution;

import com.sss.scheduler.domain.ExecutionMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class JobsExecutor {

  @Autowired(required = false)
  private Map<String, Job> jobs;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void executeJob(String jobName, ExecutionMap map) {
    Job job = loadJob(jobName);
    job.execute(map);
  }

  private Job loadJob(String jobName) {
    Job job = jobs.get(jobName);
    if(job == null) {
      throw new RuntimeException(String.format("Unable to load job with name '%s'.", jobName));
    }
    return job;
  }
}

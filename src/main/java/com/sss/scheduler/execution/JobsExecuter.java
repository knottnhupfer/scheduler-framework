package com.sss.scheduler.execution;

import com.sss.scheduler.domain.ExecutionMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobsExecuter {

  @Autowired(required = false)
  private Map<String, Job> jobs;

  public void executeJob(String jobName, Long businessObjectId, ExecutionMap map) {
    Job job = loadJob(jobName);
    job.execute(businessObjectId, map);
  }

  public void executeJobFailed(String jobName, Long businessObjectId, ExecutionMap map, JobFailedStatus status) {
    try {
      Job job = loadJob(jobName);
      job.executeJobFailed(businessObjectId, map, status);
    } catch (Exception e) {
      log.error("Error while execute job failed, no further calls executed. Reason: {}", e.getMessage(), e);
    }
  }

  private Job loadJob(String jobName) {
    Job job = jobs.get(jobName);
    if (job == null) {
      throw new RuntimeException(String.format("Unable to load job with name '%s'.", jobName));
    }
    return job;
  }
}

package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.service.JobService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestJobCreator {

  @Resource
  private JobService jobService;

  @Resource
  private JobsExecutionScheduler jobsExecutionScheduler;

  public void createJobs(String... jobNames) {
    for(String jobName : jobNames) {
      createJob(jobName);
    }
  }

  public void createJob(String jobName) {
    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(jobName);
    jobService.createJob(jobInstance);
  }

  public void createJobsAndExecute(String... jobNames) {
    createJobs(jobNames);
    jobService.retrieveAndAssignJobs();
    jobsExecutionScheduler.executeAssignedJobs();
  }
}

package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.service.JobService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TestJobCreator {

  public static final String PARAM_KEY_BUSINESS_OBJECT_ID = "BUSINESS_OBJECT_ID";
  public static final Long DEFAULT_BUSINESS_OBJECT_ID = 3L;

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
    createJob(jobName, DEFAULT_BUSINESS_OBJECT_ID);
  }

  public void createJob(String jobName, Long businessObjectId) {
    JobMap jobMap = new JobMap();
    jobMap.putLongValue(PARAM_KEY_BUSINESS_OBJECT_ID, DEFAULT_BUSINESS_OBJECT_ID);

    JobInstance jobInstance = new JobInstance();
    jobInstance.setJobName(jobName);
    jobInstance.setBusinessObjectId(businessObjectId);
    jobInstance.setJobMap(jobMap);
    jobService.createJob(jobInstance);
  }

  public void createJobsAndExecute(String... jobNames) {
    createJobs(jobNames);
    jobService.retrieveAndAssignJobs();
    jobsExecutionScheduler.executeAssignedJobs();
  }
}

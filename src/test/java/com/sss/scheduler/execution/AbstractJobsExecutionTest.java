package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import com.sss.scheduler.service.JobService;
import com.sss.scheduler.tests.IncreaseByOneJob;
import com.sss.scheduler.tests.IncreaseByOneWithBusinessObjectIdAndExecutionsJob;
import com.sss.scheduler.tests.IncreaseByOneWithBusinessObjectIdJob;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class AbstractJobsExecutionTest {

  @Resource
  protected JobService jobService;

  @Resource
  protected LockManager lockManager;

  @Resource
  protected JobRepository jobRepository;

  @Resource
  protected JobsExecutionScheduler jobsExecutionScheduler;

  protected String createNewJob(String jobName, String counterName) {
    return createNewJob(jobName, counterName, 1, 1L).get(0);
  }

  protected List<String> createNewJob(String jobName, String counterName, int jobsAmounts, Long businessObjectId) {
    List<String> jobNames = new ArrayList<>();
    for(int i = 0; i < jobsAmounts; i++) {
      JobInstance jobInstance = new JobInstance();
      jobInstance.setJobName(jobName);
      jobInstance.setBusinessObjectId(businessObjectId);

      JobMap jobMap = new JobMap();
      jobMap.putValue(IncreaseByOneJob.COUNTER_NAME, counterName);
      jobMap.putValue(IncreaseByOneWithBusinessObjectIdJob.BUSINESS_OBJECT_ID_NAME, counterName);
      jobMap.putValue(IncreaseByOneWithBusinessObjectIdAndExecutionsJob.JOB_EXECUTIONS_NAME, counterName);
      jobInstance.setJobMap(jobMap);
      jobService.createJob(jobInstance);
      jobNames.add(jobName);
    }
    return jobNames;
  }

  protected JobInstance assignAndExecuteJob(String jobName) {
    JobInstance job = jobRepository.findAllJobsByName(jobName).get(0);
    job.setExecuteBy(lockManager.getDefaultLockName());
    job.setExecutions(0L);
    jobRepository.save(job);

    jobsExecutionScheduler.executeAssignedJobs();
    return jobRepository.findById(job.getId()).get();
  }
}

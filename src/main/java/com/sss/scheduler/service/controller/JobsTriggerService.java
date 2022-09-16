package com.sss.scheduler.service.controller;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.execution.JobsExecutorService;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobsTriggerService {

  @Resource
  private JobsExecutorService jobsExecutorService;

  @Resource
  private JobRepository jobRepository;

  public void triggerJobByJobMapParam(String jobName, String key, String value, Boolean forceExecution, Boolean batchMode) {
    List<JobInstance> jobs = jobRepository.findRestartableJobsByJobName(jobName);
    jobs = jobs.stream()
            .filter(job -> job.getJobMap().getStringValue(key) != null)
            .filter(job -> job.getJobMap().getStringValue(key).equalsIgnoreCase(value))
            .collect(Collectors.toList());
    executeJobs(filterJobs(jobs, batchMode), forceExecution);
  }

  public void triggerJobByBusinessObjectId(String jobName, Long businessObjectId, Boolean forceExecution, Boolean batchMode) {
    List<JobInstance> jobs = jobRepository.findByJobNameAndBusinessObjectId(jobName, businessObjectId);
    executeJobs(filterJobs(jobs, batchMode), forceExecution);
  }

  private void executeJobs(List<JobInstance> jobInstances, Boolean forceExecution) {
    jobInstances.stream().forEach(jobInstance -> {
      if(jobInstance.getStatus().isFinal() && !forceExecution) {
        throw new IllegalArgumentException(String.format("Trying to execute JobInstance(id:%d) with final state:%s",
                        jobInstance.getId(), jobInstance.getStatus().name()));
      }
      try {
        jobsExecutorService.executeJob(jobInstance.getId());
      } catch (Exception e) {
        log.error("Error while processing jobInstance {}. Reason: {}", jobInstance, e.getMessage(), e);
        throw new IllegalArgumentException(String.format("Error while processing jobInstance %s. Reason: %s", jobInstance, e.getMessage()));
      }
    });
  }

  private List<JobInstance> filterJobs(List<JobInstance> jobs, Boolean batchMode) {
    if(batchMode) {
      return jobs;
    }
    if(jobs.size() != 1) {
      log.info("No or more then 1 job found. Found: {}", jobs.size());
      throw new IllegalArgumentException(String.format("Unable to run single job, found %d jobs.", jobs.size()));
    }
    return Collections.singletonList(jobs.get(0));
  }
}

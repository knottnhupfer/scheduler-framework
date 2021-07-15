package com.sss.scheduler.service.controller;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.execution.Job;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobsExecutionService {

  @Resource
  private List<Job> jobs;

  @Resource
  private JobRepository jobRepository;

  public void triggerJobByJobMapParam(String jobName, String key, String value, Boolean batchMode) {
    List<JobInstance> jobs = jobRepository.findRestartableJobsByJobName(jobName);
    jobs = jobs.stream()
            .filter(job -> !job.getStatus().isFinal())
            .filter(job -> job.getJobMap().getStringValue(key) != null)
            .filter(job -> job.getJobMap().getStringValue(key).equalsIgnoreCase(value))
            .collect(Collectors.toList());
    executeJobs(filterJobs(jobs, batchMode));
  }

  public void triggerJobByBusinessObjectId(String jobName, Long businessObjectId, Boolean batchMode) {
    List<JobInstance> jobs = jobRepository.findByJobNameAndBusinessObjectId(jobName, businessObjectId);
    executeJobs(filterJobs(jobs, batchMode));
  }

  private void executeJobs(List<JobInstance> jobInstances) {
    jobInstances.stream().forEach(jobInstance -> {
      try {
        Job job = loadJobByName(jobInstance.getJobName());
        executeJob(job, jobInstance);
      } catch (Exception e) {
        log.error("Error while processing jobInstance {}. Reason: {}", jobInstance, e.getMessage(), e);
      }
    });
  }

  private void executeJob(Job job, JobInstance jobInstance) {
    log.info("Execute job: {}", jobInstance.getShortDescription());
    job.execute(jobInstance.getJobMap());
    log.info("Successfully executed job '{}' with map: {}", job.getName(), jobInstance.getJobMap());
    jobInstance.setNextExecutionDate(null);
    jobInstance.setLastExecutionDate(Instant.now());
    jobInstance.setStatus(JobStatus.COMPLETED_SUCCESSFUL);
    jobInstance.setExecutionResultMessage("Executed by AdminJobsController");
    jobRepository.save(jobInstance);
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

  private Job loadJobByName(String jobName) {
    Optional<Job> foundJob = jobs.stream().filter(job -> job.getName().equalsIgnoreCase(jobName)).findFirst();
    if(!foundJob.isPresent()) {
      log.error("Error while processing request, job with name '{}' not found.", jobName);
      log.info("Supported jobs are: {}", jobs.stream().map(Job::getName).collect(Collectors.toList()));
      throw new IllegalArgumentException(String.format("Not job found with name: %s", jobName));
    }
    return foundJob.get();
  }
}

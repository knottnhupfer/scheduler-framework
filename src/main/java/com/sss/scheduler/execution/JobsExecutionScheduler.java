package com.sss.scheduler.execution;

import com.sss.scheduler.components.ExecutionConfiguration;
import com.sss.scheduler.components.ExecutionConfigurationProvider;
import com.sss.scheduler.components.RetryStrategy;
import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class JobsExecutionScheduler {

  @Resource
  private LockManager lockManager;

  @Resource
  private JobsExecutor jobsExecutor;

  @Resource
  private JobRepository jobRepository;

  @Resource
  private ExecutionConfigurationProvider executionConfigurationProvider;

  @Scheduled(fixedRateString = "${scheduler.job-execution.jobs-execution-interval}")
  public void executeAssignedJobs() {
    List<JobInstance> assignedJobs = jobRepository.findAssignedjobs(lockManager.getDefaultLockName());
    for(JobInstance job : assignedJobs) {
      log.debug("Execute job:{} with id:{}", job.getId(), job.getJobName());
      executeJob(job);
    }
  }

  private void executeJob(JobInstance job) {
    long startTime = System.currentTimeMillis();
    try {
      jobsExecutor.executeJob(job.getJobName(), job.getJobMap());

      job.setNextExecutionDate(null);
      job.setStatus(JobStatus.COMPLETED_SUCCESSFUL);
      job.setExecutionDuration(System.currentTimeMillis() - startTime);
    } catch (Exception e) {
      log.error("Error while processing job:{} with id:{}. Reason: {}",job.getJobName(), job.getId(), e.getMessage());
      log.debug("Exception stacktrace is:\n", e);
      ExecutionConfiguration executionConfiguration = executionConfigurationProvider.getExecutionConfigurationForJob(job.getJobName());

      if(executionConfiguration.getRetries() > job.getExecutions()) {
        updateExecutionParametersAfterError(job, executionConfiguration, e);
        log.info("Updated {} after {}/{} retries.", job.getJobDescription(), job.getExecutions(), executionConfiguration.getRetries());
      } else {
        log.warn("Completed errornous {} after {}/{} retries.", job.getJobDescription(), job.getExecutions(), executionConfiguration.getRetries());
        job.setStatus(JobStatus.COMPLETED_ERRONEOUS);
        job.setNextExecutionDate(null);
      }
    }

    job.setExecuteBy(null);
    job.setReservedUntil(null);
    job.setExecutionDuration(System.currentTimeMillis() - startTime);
    job.setLastExecutionDate(Instant.ofEpochMilli(startTime));
    jobRepository.save(job);
  }

  private void updateExecutionParametersAfterError(JobInstance job, ExecutionConfiguration executionConfiguration, Exception e) {
    RetryStrategy retryStrategy = executionConfiguration.getRetryStrategy();
    Instant nextExecution = retryStrategy.calculateNextExecution(job, executionConfiguration.getIntervalSeconds(), job.getExecutions());
    job.setNextExecutionDate(nextExecution);
    job.setExecutions(job.getExecutions() + 1);
    job.setStatus(JobStatus.ERRORNOUS_RETRIGGER);
    job.setExecutionResultMessage(e.getMessage());
  }
}

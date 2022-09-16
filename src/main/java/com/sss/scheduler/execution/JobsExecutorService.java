package com.sss.scheduler.execution;

import com.sss.scheduler.JobConstants;
import com.sss.scheduler.components.ExecutionConfiguration;
import com.sss.scheduler.components.ExecutionConfigurationProvider;
import com.sss.scheduler.components.RetryStrategy;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobsExecutorService {

  @Value("${scheduler.job-execution.max-execution-message-length:2048}")
  private Integer maxExecutionMessageLength;

  private final JobService jobService;

  private final JobsExecuter jobsExecuter;

  private final ExecutionConfigurationProvider executionConfigurationProvider;

  @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
  public void executeJob(Long jobInstanceId) {
    JobInstance job = jobService.loadJobInstance(jobInstanceId);
    long startTime = System.currentTimeMillis();
    try {
      job.getJobMap().putLongValue(JobConstants.MAP_KEY_JOB_EXECUTIONS, job.getExecutions());
      jobsExecuter.executeJob(job.getJobName(), job.getBusinessObjectId(), job.getJobMap());

      job.setNextExecutionDate(null);
      job.setStatus(JobStatus.COMPLETED_SUCCESSFUL);
      job.setExecutionDuration(System.currentTimeMillis() - startTime);
      job.setExecutions(job.getExecutions() + 1);
      log.debug("Successfully terminated job {}", job.getShortDescription());
    } catch (BusinessException e) {
      log.error("BUSINESS_ERROR while processing job:{} with id:{}. Reason: {}",job.getJobName(), job.getId(), e.getMessage());
      log.trace("Exception stacktrace is:\n", e);
      job.setExecutionResultMessage(trimExecutionMessageIfRequired(e.getMessage()));
      job.setStatus(JobStatus.BUSINESS_ERROR);
      job.setNextExecutionDate(null);
      job.setExecutions(job.getExecutions() + 1);
      jobsExecuter.executeJobFailed(job.getJobName(), job.getBusinessObjectId(), job.getJobMap(), JobFailedStatus.FAILED_PERMANENTLY_BUSINESS_ERROR);
    } catch (Exception e) {
      log.error("Error while processing job:{} with id:{}. Reason: {}",job.getJobName(), job.getId(), e.getMessage());
      log.trace("Exception stacktrace is:\n", e);
      ExecutionConfiguration executionConfiguration = executionConfigurationProvider.getExecutionConfigurationForJob(job.getJobName());

      if(executionConfiguration.getRetries() > job.getExecutions()) {
        updateExecutionParametersAfterError(job, executionConfiguration, e);
        log.info("Updated {} after {}/{} retries.", job.getShortDescription(), job.getExecutions() - 1, executionConfiguration.getRetries());
      } else {
        job.setStatus(JobStatus.COMPLETED_ERRONEOUS);
        job.setNextExecutionDate(null);
        log.warn("Completed errornous {} after {}/{} retries.", job.getShortDescription(), job.getExecutions(), executionConfiguration.getRetries());
        jobsExecuter.executeJobFailed(job.getJobName(), job.getBusinessObjectId(), job.getJobMap(), JobFailedStatus.FAILED_PERMANENTLY_AFTER_RETRIES);
      }
    }

    job.setExecuteBy(null);
    job.setReservedUntil(null);
    job.setExecutionDuration(System.currentTimeMillis() - startTime);
    job.setLastExecutionDate(Instant.ofEpochMilli(startTime));
  }

  private void updateExecutionParametersAfterError(JobInstance job, ExecutionConfiguration executionConfiguration, Exception e) {
    RetryStrategy retryStrategy = executionConfiguration.getRetryStrategy();
    Instant nextExecution = retryStrategy.calculateNextExecution(job, executionConfiguration.getIntervalSeconds(), job.getExecutions());
    job.setNextExecutionDate(nextExecution);
    job.setExecutions(job.getExecutions() + 1);
    job.setStatus(JobStatus.ERRORNOUS_RETRIGGER);
    job.setExecutionResultMessage(trimExecutionMessageIfRequired(e.getMessage()));
  }

  private String trimExecutionMessageIfRequired(String msg) {
    if(ObjectUtils.isEmpty(msg) || msg.length() < (maxExecutionMessageLength - 2)) {
      return msg;
    }
    return msg.substring(0, maxExecutionMessageLength - 6) + " ...";
  }
}

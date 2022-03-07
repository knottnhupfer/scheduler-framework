package com.sss.scheduler.execution;

import com.sss.scheduler.components.ExecutionConfiguration;
import com.sss.scheduler.components.ExecutionConfigurationProvider;
import com.sss.scheduler.components.RetryStrategy;
import com.sss.scheduler.domain.ExecutionMap;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class JobsExecutor {

  @Value("${scheduler.job-execution.max-execution-message-length:2048}")
  private Integer maxExecutionMessageLength;

  @Autowired(required = false)
  private Map<String, Job> jobs;

  @Resource
  private JobService jobService;

  @Resource
  private ExecutionConfigurationProvider executionConfigurationProvider;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void executeJob(Long jobInstanceId) {
    JobInstance job = jobService.loadJobInstance(jobInstanceId);
    long startTime = System.currentTimeMillis();
    try {
      executeJob(job.getJobName(), job.getJobMap());

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
      }
    }

    job.setExecuteBy(null);
    job.setReservedUntil(null);
    job.setExecutionDuration(System.currentTimeMillis() - startTime);
    job.setLastExecutionDate(Instant.ofEpochMilli(startTime));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void executeJob(String jobName, ExecutionMap map) {
    Job job = loadJob(jobName);
    job.execute(map);
  }

  private void updateExecutionParametersAfterError(JobInstance job, ExecutionConfiguration executionConfiguration, Exception e) {
    RetryStrategy retryStrategy = executionConfiguration.getRetryStrategy();
    Instant nextExecution = retryStrategy.calculateNextExecution(job, executionConfiguration.getIntervalSeconds(), job.getExecutions());
    job.setNextExecutionDate(nextExecution);
    job.setExecutions(job.getExecutions() + 1);
    job.setStatus(JobStatus.ERRORNOUS_RETRIGGER);
    job.setExecutionResultMessage(trimExecutionMessageIfRequired(e.getMessage()));
  }

  private Job loadJob(String jobName) {
    Job job = jobs.get(jobName);
    if(job == null) {
      throw new RuntimeException(String.format("Unable to load job with name '%s'.", jobName));
    }
    return job;
  }

  private String trimExecutionMessageIfRequired(String msg) {
    if(ObjectUtils.isEmpty(msg) || msg.length() < (maxExecutionMessageLength - 2)) {
      return msg;
    }
    return msg.substring(0, maxExecutionMessageLength - 6) + " ...";
  }
}

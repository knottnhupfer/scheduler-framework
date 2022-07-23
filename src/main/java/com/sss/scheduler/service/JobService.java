package com.sss.scheduler.service;

import com.sss.scheduler.JobConstants;
import com.sss.scheduler.config.SchedulerConfiguration;
import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JobService {

  @Resource
  private LockManager lockManager;

  @Resource
  private JobRepository jobRepository;

  @Resource
  private SchedulerConfiguration schedulerConfiguration;

  public void createJob(JobInstance job) {
    Assert.hasText(job.getJobName(), "job name not set");
    if (job.getJobMap() == null) {
      job.setJobMap(new JobMap());
    }
    job.setStatus(JobStatus.OPEN);
    if (job.getPriority() == null) {
      job.setPriority(JobConstants.JOB_PRIORITY_DEFAULT);
    }
    job.setExecutions(0L);
    job.setExecuteBy(null);
    if (job.getNextExecutionDate() == null) {
      job.setNextExecutionDate(Instant.now());
    }
    jobRepository.save(job);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void retrieveAndAssignJobs() {
    log.debug("Retrieve jobs to execute.");
    Instant now = Instant.now();
    jobRepository.cleanupAssignedOutdatedJobs(now);

    List<Long> jobIdsToAssign = jobRepository.findJobsToAssign(lockManager.getDefaultLockName(), schedulerConfiguration.getMaxJobsPerNode());
    if (jobIdsToAssign.isEmpty()) {
      return;
    }
    Instant reservedUntil = now.plusSeconds(schedulerConfiguration.getJobsReservationInterval());
    jobRepository.assignJobsToHostname(lockManager.getDefaultLockName(), reservedUntil, jobIdsToAssign);
    log.debug("Assign jobs {} to executer {}", jobIdsToAssign, lockManager.getDefaultLockName());
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void updateNextExecutionDate(String jobName, Long businessObjectId, Instant nextExecutionDate) {
    log.info("Update nextExecutionDate for job:{}, businessObjectId:{}, nextExecutionDate:{}", jobName, businessObjectId, nextExecutionDate);
    jobRepository.updateNextExecutionDate(jobName, businessObjectId, nextExecutionDate);
  }

  public JobInstance loadJobInstance(Long jobInstanceId) {
    Optional<JobInstance> jobInstance = jobRepository.findById(jobInstanceId);
    if (jobInstance.isPresent()) {
      return jobInstance.get();
    }
    throw new IllegalArgumentException(String.format("Unable to load JobInstance with id:%d", jobInstanceId));
  }
}

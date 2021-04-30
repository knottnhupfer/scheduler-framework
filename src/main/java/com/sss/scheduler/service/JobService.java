package com.sss.scheduler.service;

import com.sss.scheduler.config.SchedulerConfiguration;
import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobMap;
import com.sss.scheduler.domain.JobStatus;
import com.sss.scheduler.lock.LockManager;
import com.sss.scheduler.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class JobService {

  private static final int MAX_PRIORITY_AND_DEFAULT = 100;

  @Resource
  private LockManager lockManager;

  @Resource
  private JobRepository jobRepository;

  @Resource
  private SchedulerConfiguration schedulerConfiguration;

  public void createJob(JobInstance job) {
    Assert.hasText(job.getJobName(), "job name not set");
    if(job.getJobMap() == null) {
      job.setJobMap(new JobMap());
    }
    job.setStatus(JobStatus.OPEN);
    if(job.getPriority() == null || job.getPriority() > MAX_PRIORITY_AND_DEFAULT) {
      job.setPriority(MAX_PRIORITY_AND_DEFAULT);
    }
    job.setExecutions(0L);
    job.setExecuteBy(null);
    job.setNextExecutionDate(Instant.now());
    jobRepository.save(job);
  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  public void retrieveAndAssignJobs() {
    log.debug("Retrieve jobs to execute.");
    Instant now = Instant.now();
    jobRepository.cleanupAssignedOutdatedJobs(now);

    List<Long> jobIdsToAssign = jobRepository.findJobsToAssign(lockManager.getDefaultLockName(), schedulerConfiguration.getMaxJobsPerNode());
    if(jobIdsToAssign.isEmpty()) {
      return;
    }
    Instant reservedUntil = now.plusSeconds(schedulerConfiguration.getJobsReservationInterval());
    jobRepository.assignJobsToHostname(lockManager.getDefaultLockName(), reservedUntil, jobIdsToAssign);
    log.debug("Assign jobs {} to executer {}", jobIdsToAssign, lockManager.getDefaultLockName());
  }
}

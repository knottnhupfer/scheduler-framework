package com.sss.scheduler.tests;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.repository.JobRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JobTestService {

  @Resource
  private JobRepository jobRepository;

  public JobInstance getJobByName(String jobName) {
    return jobRepository.findAllJobsByName(jobName).stream().findFirst().get();
  }
}

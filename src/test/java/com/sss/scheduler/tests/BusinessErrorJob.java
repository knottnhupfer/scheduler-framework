package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.BusinessException;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

@Component("businessErrorJob")
public class BusinessErrorJob implements Job {

  @Override
  public void execute(ExecutionMap map) {
    throw new BusinessException("Business error happened.");
  }
}

package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.BusinessException;
import com.sss.scheduler.execution.Job;
import com.sss.scheduler.execution.JobFailedStatus;
import com.sss.scheduler.tests.domain.SimpleDbObject;
import com.sss.scheduler.tests.domain.SimpleDbObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component(BusinessErrorWithPersistenceJob.NAME)
public class BusinessErrorWithPersistenceJob implements Job {

  public static final String NAME = "businessErrorWithPersistenceJob";

  public static final String COUNTER_NAME = "counterName";

  @Resource
  private SimpleDbObjectRepository simpleDbObjectRepository;

  @Override
  public void execute(ExecutionMap map) {
    String counterName = map.getStringValue(COUNTER_NAME);

    SimpleDbObject object = new SimpleDbObject();
    object.setValue(counterName);
    simpleDbObjectRepository.save(object);
    throw new BusinessException("Business error happened.");
  }

  public void executeJobFailed(Long businessObjectId, ExecutionMap map, JobFailedStatus failedStatus) {
    log.info("Job failed with businessObjectId:{} and status: {}", businessObjectId, failedStatus);
  }
}

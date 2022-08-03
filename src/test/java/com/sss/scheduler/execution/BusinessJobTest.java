package com.sss.scheduler.execution;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.tests.BusinessErrorWithPersistenceJob;
import com.sss.scheduler.tests.domain.SimpleDbObject;
import com.sss.scheduler.tests.domain.SimpleDbObjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.UUID;

@SpringBootTest
public class BusinessJobTest extends AbstractJobsExecutionTest {

  @Resource
  private SimpleDbObjectRepository simpleDbObjectRepository;

  @Test
  void BusinessErrorSupportsTransactionTest() {
    jobRepository.deleteAll();
    String uniqueCounterName = UUID.randomUUID().toString();

    String newJob = createNewJob(BusinessErrorWithPersistenceJob.NAME, uniqueCounterName);
    JobInstance job = assignAndExecuteJob(BusinessErrorWithPersistenceJob.NAME);

    SimpleDbObject storedObject = simpleDbObjectRepository.findByValue(uniqueCounterName);
    Assert.notNull(storedObject, "object has not been stored");
  }
}

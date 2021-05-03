package com.sss.scheduler.configuration;

import com.sss.scheduler.components.ExecutionConfiguration;
import com.sss.scheduler.components.ExecutionConfigurationProvider;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class SchedulerConfigurationTest {

  @Resource
  private ExecutionConfigurationProvider executionConfigurationProvider;

  @Test
  public void schedulerConfigurationTest() {
    ExecutionConfiguration executionConfiguration = executionConfigurationProvider.getExecutionConfigurationForJob("someOtherJobName");
    Assert.assertNotNull(executionConfiguration);
    Assert.assertEquals("notifications", executionConfiguration.getExecutionGroup());
    Assert.assertEquals(5L, executionConfiguration.getRetries());
    Assert.assertEquals(30L, executionConfiguration.getIntervalSeconds());
    Assert.assertEquals("FibonacciRetry", executionConfiguration.getRetryStrategy().getStrategyName());

    executionConfiguration = executionConfigurationProvider.getExecutionConfigurationForJob("useDefaultJobConfig");
    Assert.assertNotNull(executionConfiguration);
    Assert.assertEquals("global", executionConfiguration.getExecutionGroup());
    Assert.assertEquals(3L, executionConfiguration.getRetries());
    Assert.assertEquals(60L, executionConfiguration.getIntervalSeconds());
    Assert.assertEquals("SimpleRetry", executionConfiguration.getRetryStrategy().getStrategyName());
  }
}

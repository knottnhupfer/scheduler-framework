package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("increaseByOneWithBusinessObjectIdAndExecutionsJob")
public class IncreaseByOneWithBusinessObjectIdAndExecutionsJob implements Job {

  public static final String JOB_EXECUTIONS_NAME = "jobExecutionsName";

  private static Map<String, Long> countersMap = new HashMap<>();

  @Override
  public void execute(Long businessObjectId, Long previousExecutions, ExecutionMap map) {
    String businessObjectIdName = map.loadStringValue(JOB_EXECUTIONS_NAME);
    countersMap.put(businessObjectIdName, previousExecutions);
  }

  public static Long getJobExecutions(String key) {
    return countersMap.get(key);
  }
}

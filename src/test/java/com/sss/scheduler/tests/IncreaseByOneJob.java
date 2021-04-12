package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("increaseByOneJob")
public class IncreaseByOneJob implements Job {

  public static final String COUNTER_NAME = "counterName";

  private static Map<String, Long> countersMap = new HashMap<>();

  @Override
  public void execute(ExecutionMap map) {
    String counterName = map.getStringValue(COUNTER_NAME);
    Long counterValue = countersMap.computeIfAbsent(counterName, s -> Long.valueOf(0));
    countersMap.put(counterName, counterValue + 1L);
  }

  public static Long getCounterValue(String key) {
    return countersMap.get(key);
  }
}

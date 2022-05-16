package com.sss.scheduler.tests;

import com.sss.scheduler.domain.ExecutionMap;
import com.sss.scheduler.execution.Job;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("increaseByOneWithBusinessObjectIdJob")
public class IncreaseByOneWithBusinessObjectIdJob implements Job {

  public static final String BUSINESS_OBJECT_ID_NAME = "businessObjectIdName";

  private static Map<String, Long> countersMap = new HashMap<>();

  @Override
  public void execute(Long businessObjectId, ExecutionMap map) {
    String businessObjectIdName = map.loadStringValue(BUSINESS_OBJECT_ID_NAME);
    countersMap.put(businessObjectIdName, businessObjectId);
  }

  public static Long getBusinessObjectId(String key) {
    return countersMap.get(key);
  }
}

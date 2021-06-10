package com.sss.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sss.scheduler.domain.JobMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;

public class JobInstanceMappingTest {

  private static ObjectMapper MAPPER = new ObjectMapper();

  @Test
  public void serializeAndDeserializeJobMapTest() throws JsonProcessingException {
    JobMap jobMap = new JobMap();
    HashMap<String, String> stringStringHashMap = new HashMap<>();
    stringStringHashMap.put("KEY", "VALUE");
    jobMap.setMappings(stringStringHashMap);
    String jobMapString = MAPPER.writeValueAsString(jobMap);
    JobMap deserializedJobMap = MAPPER.readValue(jobMapString, JobMap.class);
    Assert.assertEquals(deserializedJobMap.getMappings().get("KEY"), "VALUE");
  }

  @Test
  public void serializeSimpleDataTypesTest() throws JsonProcessingException {

    Instant now = Instant.now();
    long epochSecond = now.getEpochSecond();

    JobMap jobMap = new JobMap();
    jobMap.putValue("stringKey", "stringValue");
    jobMap.putLongValue("longKey", 123L);
    jobMap.putInstantValue("instantKey", now);

    String jobMapString = MAPPER.writeValueAsString(jobMap);
    JobMap deserializedJobMap = MAPPER.readValue(jobMapString, JobMap.class);

    Assert.assertEquals(deserializedJobMap.getStringValue("stringKey"), "stringValue");
    Assert.assertEquals(deserializedJobMap.getLongValue("longKey"), Long.valueOf(123));
    Assert.assertEquals(deserializedJobMap.getInstantValue("instantKey").getEpochSecond(), epochSecond);
  }
}

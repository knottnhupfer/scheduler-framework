package com.sss.scheduler.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JobMapFormatter {

  private static final ObjectMapper FORMATTER = new ObjectMapper();

  public static String format(JobMap jobMap) {
    try {
      return FORMATTER.writeValueAsString(jobMap);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  public static JobMap parse(String jobMapString) {
    try {
      return FORMATTER.readValue(jobMapString, JobMap.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}

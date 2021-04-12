package com.sss.scheduler.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CommandMapFormatter {

  private static final ObjectMapper FORMATTER = new ObjectMapper();

  public static String format(CommandMap commandMap) {
    try {
      return FORMATTER.writeValueAsString(commandMap);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  public static CommandMap parse(String commandMapString) {
    try {
      return FORMATTER.readValue(commandMapString, CommandMap.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}

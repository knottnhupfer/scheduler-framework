package com.sss.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sss.scheduler.domain.CommandMap;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class CommandMappingTest {

  private static ObjectMapper MAPPER = new ObjectMapper();

  @Test
  public void serializeAndDeserializeCommandMapTest() throws JsonProcessingException {
    CommandMap commandMap = new CommandMap();
    HashMap<String, String> stringStringHashMap = new HashMap<>();
    stringStringHashMap.put("KEY", "VALUE");
    commandMap.setMappings(stringStringHashMap);
    String commandMapString = MAPPER.writeValueAsString(commandMap);
    CommandMap deserializedCommandMap = MAPPER.readValue(commandMapString, CommandMap.class);
    Assert.assertEquals(deserializedCommandMap.getMappings().get("KEY"), "VALUE");
  }
}

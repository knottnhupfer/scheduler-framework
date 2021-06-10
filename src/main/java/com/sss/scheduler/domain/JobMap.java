package com.sss.scheduler.domain;

import lombok.Data;
import org.springframework.util.Assert;

import javax.persistence.Transient;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class JobMap implements ExecutionMap {

  private Map<String, String> mappings = new HashMap<>();

  public String loadStringValue(String key) {
    String value = mappings.get(key);
    if(value == null) {
      throw new IllegalStateException(String.format("No value found for key: {}", key));
    } else {
      return value;
    }
  }

  public Long loadLongValue(String key) {
    String value =loadStringValue(key);
    return Long.valueOf(value);
  }

  public Instant loadInstantValue(String key) {
    Long value = loadLongValue(key);
    return Instant.ofEpochSecond(value);
  }

  @Transient
  public String getStringValue(String key) {
    return mappings.get(key);
  }

  @Transient
  public Long getLongValue(String key) {
    String value = mappings.get(key);
    if(value == null) {
      return null;
    } else {
      return Long.valueOf(value);
    }
  }

  @Transient
  public Instant getInstantValue(String key) {
    Long value = getLongValue(key);
    if(value == null) {
      return null;
    } else {
      return Instant.ofEpochSecond(value);
    }
  }

  @Transient
  public Long getLongValue(String key, Long alternative) {
    Long longValue = getLongValue(key);
    if(longValue == null) {
      longValue = alternative;
    }
    return longValue;
  }

  public void putValue(String key, String value) {
    Assert.hasText(key, "value is not set");
    Assert.hasText(value, "value is not set");
    mappings.put(key, value);
  }

  public void putLongValue(String key, Long value) {
    Assert.hasText(key, "value is not set");
    Assert.notNull(value, "value is not set");
    mappings.put(key, value.toString());
  }

  public void putInstantValue(String key, Instant value) {
    Assert.hasText(key, "value is not set");
    Assert.notNull(value, "value is not set");
    putLongValue(key, value.getEpochSecond());
  }
}

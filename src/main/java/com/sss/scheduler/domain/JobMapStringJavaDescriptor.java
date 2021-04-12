package com.sss.scheduler.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class JobMapStringJavaDescriptor extends AbstractTypeDescriptor<JobMap> {

  public static final JobMapStringJavaDescriptor INSTANCE = new JobMapStringJavaDescriptor();

  private static final ObjectMapper FORMATTER = new ObjectMapper();

  public JobMapStringJavaDescriptor() {
    super(JobMap.class, ImmutableMutabilityPlan.INSTANCE);
  }

  @Override
  public <X> X unwrap(JobMap value, Class<X> type, WrapperOptions options) {
    if (value == null) {
      return null;
    } else if (String.class.isAssignableFrom(type)) {
      return (X) JobMapFormatter.format(value);
    }
    throw unknownUnwrap(type);
  }

  @Override
  public <X> JobMap wrap(X value, WrapperOptions options) {
    if (value == null)
      return null;

    if(String.class.isInstance(value))
      return JobMapFormatter.parse((String) value);

    throw unknownWrap(value.getClass());
  }

  @Override
  public String toString(JobMap value) {
    return JobMapFormatter.format(value);
  }

  @Override
  public JobMap fromString(String string) {
    return JobMapFormatter.parse(string);
  }

  private static String format(JobMap jobMap) {
    try {
      return FORMATTER.writeValueAsString(jobMap);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  private static JobMap parse(String jobMapString) {
    try {
      return FORMATTER.readValue(jobMapString, JobMap.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}

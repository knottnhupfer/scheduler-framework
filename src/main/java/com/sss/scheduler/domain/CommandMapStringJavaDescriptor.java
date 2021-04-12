package com.sss.scheduler.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class CommandMapStringJavaDescriptor  extends AbstractTypeDescriptor<CommandMap> {

  public static final CommandMapStringJavaDescriptor INSTANCE = new CommandMapStringJavaDescriptor();

  private static final ObjectMapper FORMATTER = new ObjectMapper();

  public CommandMapStringJavaDescriptor() {
    super(CommandMap.class, ImmutableMutabilityPlan.INSTANCE);
  }

  @Override
  public <X> X unwrap(CommandMap value, Class<X> type, WrapperOptions options) {
    if (value == null) {
      return null;
    } else if (String.class.isAssignableFrom(type)) {
      return (X) CommandMapFormatter.format(value);
    }
    throw unknownUnwrap(type);
  }

  @Override
  public <X> CommandMap wrap(X value, WrapperOptions options) {
    if (value == null)
      return null;

    if(String.class.isInstance(value))
      return CommandMapFormatter.parse((String) value);

    throw unknownWrap(value.getClass());
  }

  @Override
  public String toString(CommandMap value) {
    return CommandMapFormatter.format(value);
  }

  @Override
  public CommandMap fromString(String string) {
    return CommandMapFormatter.parse(string);
  }

  private static String format(CommandMap commandMap) {
    try {
      return FORMATTER.writeValueAsString(commandMap);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  private static CommandMap parse(String commandMapString) {
    try {
      return FORMATTER.readValue(commandMapString, CommandMap.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}

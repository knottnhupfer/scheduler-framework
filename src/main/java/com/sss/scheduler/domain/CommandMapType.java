package com.sss.scheduler.domain;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CommandMapType extends AbstractSingleColumnStandardBasicType<CommandMap> {

  public CommandMapType() {
    super(VarcharTypeDescriptor.INSTANCE, CommandMapStringJavaDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return "CommandMapString";
  }
}

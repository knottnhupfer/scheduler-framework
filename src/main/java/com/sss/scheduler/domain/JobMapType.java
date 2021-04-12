package com.sss.scheduler.domain;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class JobMapType extends AbstractSingleColumnStandardBasicType<JobMap> {

  public JobMapType() {
    super(VarcharTypeDescriptor.INSTANCE, JobMapStringJavaDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return "JobMapString";
  }
}

package com.sss.scheduler.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobsReponse {

  private List<JobShort> jobs;
}

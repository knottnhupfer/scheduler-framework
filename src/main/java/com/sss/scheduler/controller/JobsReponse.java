package com.sss.scheduler.controller;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JobsReponse {

  private List<JobShort> jobs;
}

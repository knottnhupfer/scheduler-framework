package com.sss.scheduler.controller;

import com.sss.scheduler.service.controller.JobsExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Slf4j
public class JobsExecutionController {

  @Resource
  private JobsExecutionService jobsExecutionService;

  @RequestMapping(value = "/admin/api/jobs/{jobName}/trigger", method = RequestMethod.POST)
  public ResponseEntity<Void> triggerJobWithObjectId(@NonNull @PathVariable("jobName") String jobName, @RequestParam("key") String key, @RequestParam("value") String value,
                                                     @RequestParam(value = "batch", defaultValue = "false") Boolean batchMode) {
    log.info("Trigger job '{}' with param '{}':'{}'", jobName, key, value);
    jobsExecutionService.triggerJobByJobMapParam(jobName, key, value, batchMode);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/admin/api/jobs/{jobName}/business-object-id/{id}/trigger", method = RequestMethod.POST)
  public ResponseEntity<Void> triggerJobWithObjectId(@NonNull @PathVariable("jobName") String jobName, @NonNull @PathVariable("id") Long businessObjectId,
                                                     @RequestParam(value = "batch", defaultValue = "false") Boolean batchMode) {
    log.info("Trigger job '{}' with businessObjectId:{}", jobName, businessObjectId);
    jobsExecutionService.triggerJobByBusinessObjectId(jobName, businessObjectId, batchMode);
    return ResponseEntity.ok().build();
  }
}

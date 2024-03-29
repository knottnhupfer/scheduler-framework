package com.sss.scheduler.repository;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface JobRepository extends JpaRepository<JobInstance, Long> {

  List<JobStatus> ASSIGNABLE_JOB_STATUS = Arrays.asList(JobStatus.ERRORNOUS_RETRIGGER, JobStatus.IN_PROGRESS, JobStatus.OPEN);

  List<JobStatus> RESTARTABLE_JOB_STATUS = Arrays.asList(JobStatus.OPEN, JobStatus.IN_PROGRESS, JobStatus.ERRORNOUS_RETRIGGER, JobStatus.BUSINESS_ERROR, JobStatus.COMPLETED_ERRONEOUS);

  List<JobStatus> SUCCEEDED_JOB_STATUS = Arrays.asList(JobStatus.COMPLETED_SUCCESSFUL);

  @Query("SELECT j FROM jobs j WHERE j.jobName = :jobName ORDER BY j.creationDate DESC")
  List<JobInstance> findAllJobsByName(@Param("jobName") String jobName);

  @Modifying
  @Query("UPDATE jobs j SET j.reservedUntil = NULL, j.executeBy = null WHERE j.reservedUntil <= :reservedBefore")
  void cleanupAssignedOutdatedJobs(@Param("reservedBefore") Instant reservedBefore);

  @Modifying
  @Query("UPDATE jobs j SET j.reservedUntil = :reservedUntil, j.executeBy = :executeBy, j.status = :newStatus WHERE j.id IN :jobsToAssign")
  void assignJobsToHostname(@Param("executeBy") String executeBy, @Param("reservedUntil") Instant reservedUntil,
                            @Param("newStatus") JobStatus newStatus, @Param("jobsToAssign") List<Long> jobsToAssign);

  @Modifying
  @Query("UPDATE jobs j SET j.reservedUntil = :reservedUntil, j.executeBy = :executeBy WHERE j.id IN :jobsToAssign")
  void assignJobsToHostname(@Param("executeBy") String executeBy, @Param("reservedUntil") Instant reservedUntil,
                            @Param("jobsToAssign") List<Long> jobsToAssign);

  default List<Long> findJobsToAssign(String executeBy, int maxJobs) {
    List<Long> alreadyAssignedJobs = findAlreadyAssignedJobs(executeBy);
    if(alreadyAssignedJobs.size() >= maxJobs) {
      return alreadyAssignedJobs;
    }
    alreadyAssignedJobs.addAll(findJobsToAssign(
            Instant.now(), ASSIGNABLE_JOB_STATUS, PageRequest.of(0, maxJobs - alreadyAssignedJobs.size())));
    return alreadyAssignedJobs;
  }

  @Query("SELECT c.id FROM jobs c WHERE c.reservedUntil IS NULL AND c.status IN :openStatus AND c.nextExecutionDate < :now ORDER BY c.priority DESC, c.creationDate ASC")
  List<Long> findJobsToAssign(
          @Param("now") Instant now, @Param("openStatus") List<JobStatus> openStatus, Pageable pageable);

  @Query("SELECT c.id FROM jobs c WHERE c.executeBy = :executedBy ORDER BY c.creationDate ASC")
  List<Long> findAlreadyAssignedJobs(@Param("executedBy") String executedBy);

  @Query("SELECT c FROM jobs c WHERE c.executeBy = :executedBy ORDER BY c.priority DESC, c.creationDate ASC")
  List<JobInstance> findAssignedjobs(@Param("executedBy") String executedBy);

  default List<JobInstance> findJobsByStates(List<JobStatus> status, Instant from, Instant to, int maxJobs) {
    return findJobsByStates(status, from, to, PageRequest.of(0, maxJobs));
  }

  @Query("SELECT j FROM jobs j WHERE j.creationDate >= :fromDate AND j.creationDate <= :toDate " +
                 "AND j.status IN :status ORDER BY j.creationDate ASC")
  List<JobInstance> findJobsByStates(
          @Param("status") List<JobStatus> status, @Param("fromDate") Instant from, @Param("toDate") Instant to, Pageable pageable);

  @Transactional
  default void deleteSuccessfullyTerminatedJobsOlderThen(Long cleanupThresholdMinutes) {
    Instant threshold = Instant.now().minus(cleanupThresholdMinutes, ChronoUnit.MINUTES);
    deleteJobsByIds(SUCCEEDED_JOB_STATUS, threshold);
  }

  @Modifying
  @Query("DELETE FROM jobs j WHERE j.lastExecutionDate <= :cleanupThreshold AND j.status IN :status")
  void deleteJobsByIds(@Param("status") List<JobStatus> status, @Param("cleanupThreshold") Instant cleanupThreshold);

  @Modifying
  @Query("UPDATE jobs j SET j.nextExecutionDate = :nextExecutionDate WHERE j.jobName = :jobName AND j.businessObjectId = :businessObjectId")
  void updateNextExecutionDate(@Param("jobName") String jobName, @Param("businessObjectId") Long businessObjectId,
                               @Param("nextExecutionDate") Instant nextExecutionDate);

  List<JobInstance> findByJobNameAndBusinessObjectId(String jobName, Long businessObjectId);

  @Query("SELECT j FROM jobs j WHERE j.jobName = :jobName AND j.status IN :status ORDER BY j.creationDate ASC")
  List<JobInstance> findJobsByJobNameAndStatus(@Param("jobName") String jobName, @Param("status") Collection<JobStatus> status);

  default List<JobInstance> findRestartableJobsByJobName(String jobName) {
    return findJobsByJobNameAndStatus(jobName, RESTARTABLE_JOB_STATUS);
  }
}

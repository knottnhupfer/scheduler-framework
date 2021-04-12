package com.sss.scheduler.repository;

import com.sss.scheduler.domain.JobInstance;
import com.sss.scheduler.domain.JobStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface JobRepository extends JpaRepository<JobInstance, Long> {

  @Query("SELECT j FROM jobs j WHERE j.jobName = :jobName ORDER BY j.creationDate DESC")
  List<JobInstance> findAllJobsByName(@Param("jobName") String jobName);

  @Modifying
  @Query("UPDATE jobs j SET j.reservedUntil = NULL, j.executeBy = null WHERE j.reservedUntil <= :reservedBefore")
  void cleanupAssignedOutdatedJobs(@Param("reservedBefore") Instant reservedBefore);

  @Modifying
  @Query("UPDATE jobs j SET j.reservedUntil = :reservedUntil, j.executeBy = :executeBy, j.status = :newStatus WHERE j.id IN :jobsToAssign")
  void assignJobsToHostname(@Param("executeBy") String executeBy, @Param("reservedUntil") Instant reservedUntil,
                            @Param("newStatus") JobStatus newStatus, @Param("jobsToAssign") List<Long> jobsToAssign);

  default List<Long> findJobsToAssign(String executeBy, int maxJobs) {
    List<Long> alreadyAssignedJobs = findJobsToAssign(executeBy);
    if(alreadyAssignedJobs.size() >= maxJobs) {
      return alreadyAssignedJobs;
    }
    alreadyAssignedJobs.addAll(findJobsToAssign(PageRequest.of(0, maxJobs - alreadyAssignedJobs.size())));
    return alreadyAssignedJobs;
  }

  @Query("SELECT c.id FROM jobs c WHERE c.reservedUntil IS NULL ORDER BY c.creationDate ASC")
  List<Long> findJobsToAssign(Pageable pageable);

  @Query("SELECT c.id FROM jobs c WHERE c.executeBy = :executedBy ORDER BY c.creationDate ASC")
  List<Long> findJobsToAssign(@Param("executedBy") String executedBy);

  @Query("SELECT c FROM jobs c WHERE c.executeBy = :executedBy ORDER BY c.priority ASC, c.creationDate ASC")
  List<JobInstance> findAssignedjobs(@Param("executedBy") String executedBy);
}

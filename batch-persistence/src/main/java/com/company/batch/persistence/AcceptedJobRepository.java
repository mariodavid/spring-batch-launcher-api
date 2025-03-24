package com.company.batch.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AcceptedJobRepository extends CrudRepository<AcceptedJob, String> {

    Optional<AcceptedJob> findByExternalJobExecutionId(String externalJobExecutionId);

}
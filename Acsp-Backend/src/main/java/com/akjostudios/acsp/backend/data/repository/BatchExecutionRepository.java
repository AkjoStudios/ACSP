package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.entity.BatchExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface BatchExecutionRepository extends JpaRepository<BatchExecution, Long> {}
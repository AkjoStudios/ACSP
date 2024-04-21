package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.entity.CommandExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface CommandExecutionRepository extends JpaRepository<CommandExecution, Long> {}
package com.akjostudios.acsp.backend.data.repository.bot.log.command;

import com.akjostudios.acsp.backend.data.entity.bot.log.command.CommandResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface CommandResponseRepository extends JpaRepository<CommandResponse, Long> {}
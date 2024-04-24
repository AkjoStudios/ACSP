package com.akjostudios.acsp.backend.data.repository.bot.log.command;

import com.akjostudios.acsp.backend.data.entity.bot.log.command.CommandResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public interface CommandResponseRepository extends JpaRepository<CommandResponse, Long> {
    Optional<CommandResponse> findFirstByMessageId(long messageId);
}
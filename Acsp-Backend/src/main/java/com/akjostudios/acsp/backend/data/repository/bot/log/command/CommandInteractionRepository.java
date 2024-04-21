package com.akjostudios.acsp.backend.data.repository.bot.log.command;

import com.akjostudios.acsp.backend.data.entity.bot.log.command.CommandInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface CommandInteractionRepository extends JpaRepository<CommandInteraction, Long> {}
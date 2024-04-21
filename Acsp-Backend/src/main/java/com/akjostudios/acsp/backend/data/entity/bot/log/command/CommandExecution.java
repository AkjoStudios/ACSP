package com.akjostudios.acsp.backend.data.entity.bot.log.command;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity(name = "command_executions")
public class CommandExecution {
    @Id
    @Column(name = "execution_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long executionId;

    @Column(name = "message_id", nullable = false)
    private long messageId;

    @Column(name = "channel_id", nullable = false)
    private long channelId;

    @Column(name = "command_name", nullable = false)
    private String commandName;

    @Column(name = "subcommand_name")
    private String subcommandName;

    @Column(name = "finished_at", nullable = false)
    private Instant finishedAt;

    @OneToMany(mappedBy = "execution", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandResponse> responses;
}
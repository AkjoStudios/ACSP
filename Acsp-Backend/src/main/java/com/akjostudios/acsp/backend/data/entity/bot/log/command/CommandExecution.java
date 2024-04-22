package com.akjostudios.acsp.backend.data.entity.bot.log.command;

import com.akjostudios.acsp.common.model.bot.log.command.CommandExecutionDao;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

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

    @Column(name = "finished", nullable = false)
    private boolean finished;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "execution", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandResponse> responses;

    public @NotNull CommandExecutionDao toDao() {
        return new CommandExecutionDao(
                executionId,
                messageId,
                channelId,
                commandName,
                subcommandName,
                finished,
                finishedAt,
                createdAt,
                responses.stream().map(CommandResponse::toDao).toList()
        );
    }
}
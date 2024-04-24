package com.akjostudios.acsp.backend.data.entity.bot.log.command;

import com.akjostudios.acsp.common.model.bot.log.command.CommandExecutionDao;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "command_name", nullable = false)
    private String commandName;

    @Column(name = "command_args", nullable = false)
    @Type(JsonType.class)
    private Map<String, String> commandArgs;

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
                userId,
                commandName,
                commandArgs,
                subcommandName,
                finished,
                finishedAt,
                createdAt,
                responses.stream().map(CommandResponse::toDao).toList()
        );
    }
}
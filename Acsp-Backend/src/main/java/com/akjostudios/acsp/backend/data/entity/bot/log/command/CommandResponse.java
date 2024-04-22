package com.akjostudios.acsp.backend.data.entity.bot.log.command;

import com.akjostudios.acsp.common.model.bot.log.command.CommandResponseDao;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Data
@Entity(name = "command_responses")
public class CommandResponse {
    @Id
    @Column(name = "response_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long responseId;

    @ManyToOne
    @JoinColumn(name = "execution_id", nullable = false)
    private CommandExecution execution;

    @Column(name = "message_id", nullable = false)
    private long messageId;

    @Column(name = "channel_id", nullable = false)
    private long channelId;

    @Column(name = "finished_at", nullable = false)
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandInteraction> interactions;

    public @NotNull CommandResponseDao toDao() {
        return new CommandResponseDao(
                responseId,
                messageId,
                channelId,
                finishedAt,
                createdAt,
                interactions.stream().map(CommandInteraction::toDao).toList()
        );
    }
}
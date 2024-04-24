package com.akjostudios.acsp.backend.data.entity.bot.log.command;

import com.akjostudios.acsp.common.model.bot.log.command.CommandResponseDao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Data
@RequiredArgsConstructor
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

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public @NotNull CommandResponseDao toDao() {
        return new CommandResponseDao(
                responseId,
                messageId,
                channelId,
                createdAt
        );
    }
}
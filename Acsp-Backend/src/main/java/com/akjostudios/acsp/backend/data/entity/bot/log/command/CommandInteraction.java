package com.akjostudios.acsp.backend.data.entity.bot.log.command;

import com.akjostudios.acsp.common.model.bot.log.command.CommandInteractionDao;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Data
@Entity(name = "command_interactions")
public class CommandInteraction {
    @Id
    @Column(name = "interaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long interactionId;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false)
    private CommandResponse response;

    @Column(name = "interaction_name", nullable = false)
    private String interactionName;

    @Column(name = "finished", nullable = false)
    private boolean finished;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    public @NotNull CommandInteractionDao toDao() {
        return new CommandInteractionDao(
                interactionId,
                interactionName,
                finished,
                finishedAt,
                createdAt
        );
    }
}
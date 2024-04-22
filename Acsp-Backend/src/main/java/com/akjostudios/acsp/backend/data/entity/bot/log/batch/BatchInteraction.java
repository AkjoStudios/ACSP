package com.akjostudios.acsp.backend.data.entity.bot.log.batch;

import com.akjostudios.acsp.common.model.bot.log.batch.BatchInteractionDao;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Data
@Entity(name = "batch_interactions")
public class BatchInteraction {
    @Id
    @Column(name = "interaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long interactionId;

    @Column(name = "interaction_name", nullable = false)
    private String interactionName;

    @Column(name = "finished", nullable = false)
    private boolean finished;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false)
    private BatchResponse response;

    public @NotNull BatchInteractionDao toDao() {
        return new BatchInteractionDao(
                interactionId,
                interactionName,
                finished,
                finishedAt,
                createdAt
        );
    }
}
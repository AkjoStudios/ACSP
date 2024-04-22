package com.akjostudios.acsp.backend.data.entity.bot.log.batch;

import com.akjostudios.acsp.common.model.bot.log.batch.BatchResponseDao;
import jakarta.persistence.*;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Data
@Entity(name = "batch_responses")
public class BatchResponse {
    @Id
    @Column(name = "response_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long responseId;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private BatchExecution execution;

    @Column(name = "message_id", nullable = false)
    private long messageId;

    @Column(name = "channel_id", nullable = false)
    private long channelId;

    @Column(name = "finished_at", nullable = false)
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchInteraction> interactions;

    public @NotNull BatchResponseDao toDao() {
        return new BatchResponseDao(
                responseId,
                messageId,
                channelId,
                finishedAt,
                createdAt,
                interactions.stream().map(BatchInteraction::toDao).toList()
        );
    }
}
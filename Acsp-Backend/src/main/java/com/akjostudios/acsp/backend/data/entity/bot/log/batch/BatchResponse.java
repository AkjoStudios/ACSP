package com.akjostudios.acsp.backend.data.entity.bot.log.batch;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchInteraction> interactions;
}
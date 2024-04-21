package com.akjostudios.acsp.backend.data.entity.bot.log.batch;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity(name = "batch_interactions")
public class BatchInteraction {
    @Id
    @Column(name = "interaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long interactionId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false)
    private BatchResponse response;
}
package com.akjostudios.acsp.backend.data.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "batch_interactions")
public class BatchInteraction {
    @Id
    @Column(name = "interaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long interactionId;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false)
    private BatchResponse response;
}
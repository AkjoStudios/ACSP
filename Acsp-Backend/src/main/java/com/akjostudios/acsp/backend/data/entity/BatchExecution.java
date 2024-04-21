package com.akjostudios.acsp.backend.data.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity(name = "batch_executions")
public class BatchExecution {
    @Id
    @Column(name = "batch_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long batchId;

    @Column(name = "finished", nullable = false)
    private boolean finished;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @OneToMany(mappedBy = "execution", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchResponse> responses;
}
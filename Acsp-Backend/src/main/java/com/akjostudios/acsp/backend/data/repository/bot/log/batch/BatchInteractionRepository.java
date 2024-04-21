package com.akjostudios.acsp.backend.data.repository.bot.log.batch;

import com.akjostudios.acsp.backend.data.entity.bot.log.batch.BatchInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public interface BatchInteractionRepository extends JpaRepository<BatchInteraction, Long> {}
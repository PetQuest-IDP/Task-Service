package org.acs.idp.taskservice.model.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskDto(UUID id,
                      UUID questId,
                      String title,
                      String description,
                      boolean completed,
                      Instant completedAt) {
}

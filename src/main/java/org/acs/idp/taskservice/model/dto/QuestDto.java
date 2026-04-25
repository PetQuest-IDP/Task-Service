package org.acs.idp.taskservice.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuestDto(UUID id,
                       String ownerEmail,
                       String helperEmail,     // null dacă e OPEN
                       String title,
                       String description,
                       QuestStatus status,
                       Instant createdAt,
                       List<TaskDto> tasks) {
}

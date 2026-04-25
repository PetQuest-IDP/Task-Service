package org.acs.idp.taskservice.model.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(UUID id,
                         UUID questId,
                         String authorEmail,
                         String content,
                         Instant createdAt) {
}

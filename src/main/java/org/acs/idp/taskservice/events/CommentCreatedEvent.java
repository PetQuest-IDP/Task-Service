package org.acs.idp.taskservice.events;

import java.time.Instant;
import java.util.UUID;

public record CommentCreatedEvent(UUID questId,
                                  String authorEmail,
                                  String recipientEmail,
                                  String contentPreview,
                                  Instant occurredAt) {
}

package org.acs.idp.taskservice.events;

import java.time.Instant;
import java.util.UUID;

public record TaskCompletedEvent(UUID questId,
                                 UUID taskId,
                                 String taskTitle,
                                 String ownerEmail,
                                 String helperEmail,
                                 Instant occurredAt) {
}

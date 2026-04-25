package org.acs.idp.taskservice.events;

import java.time.Instant;
import java.util.UUID;

public record QuestCompletedEvent(UUID questId,
                                  String questTitle,
                                  String ownerEmail,
                                  String helperEmail,
                                  Instant occurredAt) {
}

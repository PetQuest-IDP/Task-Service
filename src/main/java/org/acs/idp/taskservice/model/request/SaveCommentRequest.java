package org.acs.idp.taskservice.model.request;

import java.util.UUID;

public record SaveCommentRequest(UUID questId,
                                 String authorEmail,
                                 String content) {
}

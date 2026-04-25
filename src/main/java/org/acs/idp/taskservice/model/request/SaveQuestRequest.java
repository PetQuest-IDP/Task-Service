package org.acs.idp.taskservice.model.request;

import java.util.List;

public record SaveQuestRequest(String ownerEmail,
                               String title,
                               String description,
                               List<SaveTaskRequest> tasks) {
}

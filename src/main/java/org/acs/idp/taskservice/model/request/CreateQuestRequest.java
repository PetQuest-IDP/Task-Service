package org.acs.idp.taskservice.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateQuestRequest(@NotBlank @Size(max = 200) String title,
                                 @Size(max = 2000) String description,
                                 @NotNull @Valid List<CreateTaskRequest> tasks) {
}

package org.acs.idp.taskservice.controller;

import jakarta.validation.Valid;
import org.acs.idp.taskservice.model.dto.TaskDto;
import org.acs.idp.taskservice.model.request.AddTaskRequest;
import org.acs.idp.taskservice.security.CurrentUser;
import org.acs.idp.taskservice.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/quests/{questId}/tasks")
    public ResponseEntity<TaskDto> addTask(@PathVariable UUID questId,
                                           @Valid @RequestBody AddTaskRequest request) {
        String callerEmail = CurrentUser.getEmail();
        TaskDto created = taskService.addTask(questId, callerEmail, request);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/tasks/{id}/complete")
    public ResponseEntity<TaskDto> complete(@PathVariable UUID id) {
        String callerEmail = CurrentUser.getEmail();
        return ResponseEntity.ok(taskService.completeTask(id, callerEmail));
    }
}

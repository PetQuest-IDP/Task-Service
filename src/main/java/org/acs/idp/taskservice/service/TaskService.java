package org.acs.idp.taskservice.service;

import org.acs.idp.taskservice.client.DbClientService;
import org.acs.idp.taskservice.events.TaskCompletedEvent;
import org.acs.idp.taskservice.exception.BadRequestException;
import org.acs.idp.taskservice.exception.ForbiddenException;
import org.acs.idp.taskservice.exception.NotFoundException;
import org.acs.idp.taskservice.messaging.NotificationPublisher;
import org.acs.idp.taskservice.model.dto.QuestDto;
import org.acs.idp.taskservice.model.dto.QuestStatus;
import org.acs.idp.taskservice.model.dto.TaskDto;
import org.acs.idp.taskservice.model.request.AddTaskRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TaskService {
    private final DbClientService dbClient;
    private final NotificationPublisher publisher;

    public TaskService(DbClientService dbClient, NotificationPublisher publisher) {
        this.dbClient = dbClient;
        this.publisher = publisher;
    }

    public TaskDto addTask(UUID questId, String callerEmail, AddTaskRequest request) {
        QuestDto quest = dbClient.findQuestById(questId);
        if (quest == null) {
            throw new NotFoundException("Quest not found: " + questId);
        }
        if (!quest.ownerEmail().equals(callerEmail)) {
            throw new ForbiddenException("Only the owner can add tasks");
        }
        if (quest.status() == QuestStatus.COMPLETED) {
            throw new BadRequestException("Cannot add tasks to a completed quest");
        }

        return dbClient.addTaskToQuest(questId, request);
    }

    public TaskDto completeTask(UUID taskId, String callerEmail) {
        TaskDto task = dbClient.findTaskById(taskId);
        if (task == null) {
            throw new NotFoundException("Task not found: " + taskId);
        }
        if (task.completed()) {
            throw new BadRequestException("Task is already completed");
        }

        QuestDto quest = dbClient.findQuestById(task.questId());
        if (quest == null) {
            throw new NotFoundException("Parent quest not found");
        }
        if (quest.status() != QuestStatus.ACCEPTED) {
            throw new BadRequestException("Quest is not in an active state");
        }
        if (quest.helperEmail() == null || !quest.helperEmail().equals(callerEmail)) {
            throw new ForbiddenException("Only the assigned helper can complete tasks");
        }

        TaskDto updated = dbClient.completeTask(taskId);

        publisher.publishTaskCompleted(new TaskCompletedEvent(
                quest.id(),
                updated.id(),
                updated.title(),
                quest.ownerEmail(),
                quest.helperEmail(),
                Instant.now()));

        return updated;
    }
}

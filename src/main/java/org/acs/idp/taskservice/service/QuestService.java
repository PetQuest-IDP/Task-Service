package org.acs.idp.taskservice.service;

import org.acs.idp.taskservice.client.DbClientService;
import org.acs.idp.taskservice.events.QuestAcceptedEvent;
import org.acs.idp.taskservice.events.QuestCompletedEvent;
import org.acs.idp.taskservice.exception.BadRequestException;
import org.acs.idp.taskservice.exception.ForbiddenException;
import org.acs.idp.taskservice.exception.NotFoundException;
import org.acs.idp.taskservice.messaging.NotificationPublisher;
import org.acs.idp.taskservice.model.dto.QuestDto;
import org.acs.idp.taskservice.model.dto.QuestStatus;
import org.acs.idp.taskservice.model.request.CreateQuestRequest;
import org.acs.idp.taskservice.model.request.SaveQuestRequest;
import org.acs.idp.taskservice.model.request.SaveTaskRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class QuestService {
    private final DbClientService dbClient;
    private final NotificationPublisher publisher;

    public QuestService(DbClientService dbClient, NotificationPublisher publisher) {
        this.dbClient = dbClient;
        this.publisher = publisher;
    }

    // Create: owner creates quest with one or more tasks
    public QuestDto createQuest(String ownerEmail, CreateQuestRequest request) {
        if (request.tasks() == null || request.tasks().isEmpty()) {
            throw new BadRequestException("A quest must have at least one task");
        }

        List<SaveTaskRequest> tasks = request.tasks().stream()
                .map(t -> new SaveTaskRequest(t.title(), t.description()))
                .toList();

        SaveQuestRequest save = new SaveQuestRequest(
                ownerEmail,
                request.title(),
                request.description(),
                tasks);

        return dbClient.saveQuest(save);
    }

    public List<QuestDto> getOpenQuests() {
        return dbClient.findQuestsByStatus(QuestStatus.OPEN);
    }

    public List<QuestDto> getMyAcceptedQuests(String helperEmail) {
        return dbClient.findQuestsByHelperEmail(helperEmail);
    }

    public List<QuestDto> getMyOwnedQuests(String ownerEmail) {
        return dbClient.findQuestsByOwnerEmail(ownerEmail);
    }

    public QuestDto getQuest(UUID questId) {
        QuestDto quest = dbClient.findQuestById(questId);
        if (quest == null) {
            throw new NotFoundException("Quest not found: " + questId);
        }
        return quest;
    }

    public QuestDto acceptQuest(UUID questId, String helperEmail) {
        QuestDto quest = getQuest(questId);

        if (quest.status() != QuestStatus.OPEN) {
            throw new BadRequestException("Quest is not open for acceptance");
        }
        if (quest.ownerEmail().equals(helperEmail)) {
            throw new BadRequestException("You cannot accept your own quest");
        }

        QuestDto updated = dbClient.acceptQuest(questId, helperEmail);

        publisher.publishQuestAccepted(new QuestAcceptedEvent(
                updated.id(),
                updated.title(),
                updated.ownerEmail(),
                updated.helperEmail(),
                Instant.now()));

        return updated;
    }

    public QuestDto completeQuest(UUID questId, String callerEmail) {
        QuestDto quest = getQuest(questId);

        if (!quest.ownerEmail().equals(callerEmail)) {
            throw new ForbiddenException("Only the owner can complete the quest");
        }
        if (quest.status() == QuestStatus.COMPLETED) {
            throw new BadRequestException("Quest is already completed");
        }

        QuestDto updated = dbClient.completeQuest(questId);

        publisher.publishQuestCompleted(new QuestCompletedEvent(
                updated.id(),
                updated.title(),
                updated.ownerEmail(),
                updated.helperEmail(),
                Instant.now()));

        return updated;
    }
}

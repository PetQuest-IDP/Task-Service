package org.acs.idp.taskservice.service;

import org.acs.idp.taskservice.client.DbClientService;
import org.acs.idp.taskservice.events.CommentCreatedEvent;
import org.acs.idp.taskservice.exception.ForbiddenException;
import org.acs.idp.taskservice.exception.NotFoundException;
import org.acs.idp.taskservice.messaging.NotificationPublisher;
import org.acs.idp.taskservice.model.dto.CommentDto;
import org.acs.idp.taskservice.model.dto.QuestDto;
import org.acs.idp.taskservice.model.request.CreateCommentRequest;
import org.acs.idp.taskservice.model.request.SaveCommentRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private static final int CONTENT_PREVIEW_LENGTH = 80;

    private final DbClientService dbClient;
    private final NotificationPublisher publisher;

    public CommentService(DbClientService dbClient, NotificationPublisher publisher) {
        this.dbClient = dbClient;
        this.publisher = publisher;
    }

    public List<CommentDto> listCommentsForQuest(UUID questId) {
        QuestDto quest = dbClient.findQuestById(questId);
        if (quest == null) {
            throw new NotFoundException("Quest not found: " + questId);
        }
        return dbClient.findCommentsByQuestId(questId);
    }

    public CommentDto addComment(UUID questId, String authorEmail, CreateCommentRequest request) {
        QuestDto quest = dbClient.findQuestById(questId);
        if (quest == null) {
            throw new NotFoundException("Quest not found: " + questId);
        }

        boolean isOwner = quest.ownerEmail().equals(authorEmail);
        boolean isHelper = authorEmail.equals(quest.helperEmail());

        if (!isOwner && !isHelper) {
            throw new ForbiddenException("Only the owner or assigned helper can comment");
        }

        CommentDto saved = dbClient.saveComment(
                new SaveCommentRequest(questId, authorEmail, request.content()));

        String recipientEmail = isOwner ? quest.helperEmail() : quest.ownerEmail();

        if (recipientEmail != null) {
            publisher.publishCommentCreated(new CommentCreatedEvent(
                    questId,
                    authorEmail,
                    recipientEmail,
                    preview(request.content()),
                    Instant.now()));
        }

        return saved;
    }

    private String preview(String content) {
        if (content == null) return "";
        return content.length() <= CONTENT_PREVIEW_LENGTH
                ? content
                : content.substring(0, CONTENT_PREVIEW_LENGTH) + "...";
    }
}

package org.acs.idp.taskservice.client;

import org.acs.idp.taskservice.model.dto.CommentDto;
import org.acs.idp.taskservice.model.dto.QuestDto;
import org.acs.idp.taskservice.model.dto.QuestStatus;
import org.acs.idp.taskservice.model.dto.TaskDto;
import org.acs.idp.taskservice.model.request.AddTaskRequest;
import org.acs.idp.taskservice.model.request.SaveCommentRequest;
import org.acs.idp.taskservice.model.request.SaveQuestRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class DbClientService {
    private final RestClient restClient;

    public DbClientService(@Value("${db-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<QuestDto> findAllQuests() {
        try {
            return restClient.get()
                    .uri("/quests")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuestDto>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<QuestDto> findQuestsByStatus(QuestStatus status) {
        try {
            return restClient.get()
                    .uri("/quests?status={status}", status.name())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuestDto>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<QuestDto> findQuestsByHelperEmail(String helperEmail) {
        try {
            return restClient.get()
                    .uri("/quests?helperEmail={email}", helperEmail)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuestDto>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<QuestDto> findQuestsByOwnerEmail(String ownerEmail) {
        try {
            return restClient.get()
                    .uri("/quests?ownerEmail={email}", ownerEmail)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuestDto>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public QuestDto findQuestById(UUID id) {
        try {
            return restClient.get()
                    .uri("/quests/{id}", id)
                    .retrieve()
                    .body(QuestDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    public QuestDto saveQuest(SaveQuestRequest request) {
        return restClient.post()
                .uri("/quests")
                .body(request)
                .retrieve()
                .body(QuestDto.class);
    }

    public QuestDto acceptQuest(UUID questId, String helperEmail) {
        return restClient.patch()
                .uri("/quests/{id}/accept?helperEmail={email}", questId, helperEmail)
                .retrieve()
                .body(QuestDto.class);
    }

    public QuestDto completeQuest(UUID questId) {
        return restClient.patch()
                .uri("/quests/{id}/complete", questId)
                .retrieve()
                .body(QuestDto.class);
    }

    public TaskDto findTaskById(UUID id) {
        try {
            return restClient.get()
                    .uri("/tasks/{id}", id)
                    .retrieve()
                    .body(TaskDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    public TaskDto addTaskToQuest(UUID questId, AddTaskRequest request) {
        return restClient.post()
                .uri("/quests/{questId}/tasks", questId)
                .body(request)
                .retrieve()
                .body(TaskDto.class);
    }

    public TaskDto completeTask(UUID taskId) {
        return restClient.patch()
                .uri("/tasks/{id}/complete", taskId)
                .retrieve()
                .body(TaskDto.class);
    }

    public List<CommentDto> findCommentsByQuestId(UUID questId) {
        try {
            return restClient.get()
                    .uri("/quests/{questId}/comments", questId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CommentDto>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public CommentDto saveComment(SaveCommentRequest request) {
        return restClient.post()
                .uri("/comments")
                .body(request)
                .retrieve()
                .body(CommentDto.class);
    }
}

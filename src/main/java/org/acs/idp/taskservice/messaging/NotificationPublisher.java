package org.acs.idp.taskservice.messaging;

import org.acs.idp.taskservice.events.CommentCreatedEvent;
import org.acs.idp.taskservice.events.QuestAcceptedEvent;
import org.acs.idp.taskservice.events.QuestCompletedEvent;
import org.acs.idp.taskservice.events.RoutingKeys;
import org.acs.idp.taskservice.events.TaskCompletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public NotificationPublisher(RabbitTemplate rabbitTemplate,
                                 @Value("${notifications.exchange}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void publishQuestAccepted(QuestAcceptedEvent event) {
        rabbitTemplate.convertAndSend(exchange, RoutingKeys.QUEST_ACCEPTED, event);
    }

    public void publishQuestCompleted(QuestCompletedEvent event) {
        rabbitTemplate.convertAndSend(exchange, RoutingKeys.QUEST_COMPLETED, event);
    }

    public void publishTaskCompleted(TaskCompletedEvent event) {
        rabbitTemplate.convertAndSend(exchange, RoutingKeys.TASK_COMPLETED, event);
    }

    public void publishCommentCreated(CommentCreatedEvent event) {
        rabbitTemplate.convertAndSend(exchange, RoutingKeys.COMMENT_CREATED, event);
    }
}

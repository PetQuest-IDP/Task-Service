package org.acs.idp.taskservice.controller;

import jakarta.validation.Valid;
import org.acs.idp.taskservice.model.dto.QuestDto;
import org.acs.idp.taskservice.model.request.CreateQuestRequest;
import org.acs.idp.taskservice.security.CurrentUser;
import org.acs.idp.taskservice.service.QuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quests")
public class QuestController {
    private final QuestService questService;

    public QuestController(QuestService questService) {
        this.questService = questService;
    }

    @PostMapping
    public ResponseEntity<QuestDto> create(@Valid @RequestBody CreateQuestRequest request) {
        String ownerEmail = CurrentUser.getEmail();
        QuestDto created = questService.createQuest(ownerEmail, request);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<QuestDto>> feed() {
        return ResponseEntity.ok(questService.getOpenQuests());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<QuestDto>> myAccepted() {
        String helperEmail = CurrentUser.getEmail();
        return ResponseEntity.ok(questService.getMyAcceptedQuests(helperEmail));
    }

    @GetMapping("/owned")
    public ResponseEntity<List<QuestDto>> myOwned() {
        String ownerEmail = CurrentUser.getEmail();
        return ResponseEntity.ok(questService.getMyOwnedQuests(ownerEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestDto> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(questService.getQuest(id));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<QuestDto> accept(@PathVariable UUID id) {
        String helperEmail = CurrentUser.getEmail();
        return ResponseEntity.ok(questService.acceptQuest(id, helperEmail));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<QuestDto> complete(@PathVariable UUID id) {
        String callerEmail = CurrentUser.getEmail();
        return ResponseEntity.ok(questService.completeQuest(id, callerEmail));
    }
}

package org.acs.idp.taskservice.controller;

import jakarta.validation.Valid;
import org.acs.idp.taskservice.model.dto.CommentDto;
import org.acs.idp.taskservice.model.request.CreateCommentRequest;
import org.acs.idp.taskservice.security.CurrentUser;
import org.acs.idp.taskservice.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quests/{questId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> list(@PathVariable UUID questId) {
        return ResponseEntity.ok(commentService.listCommentsForQuest(questId));
    }

    @PostMapping
    public ResponseEntity<CommentDto> add(@PathVariable UUID questId,
                                          @Valid @RequestBody CreateCommentRequest request) {
        String authorEmail = CurrentUser.getEmail();
        return ResponseEntity.ok(commentService.addComment(questId, authorEmail, request));
    }
}

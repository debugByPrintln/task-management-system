package com.melnikov.taskmanagementsystem.controller;

import com.melnikov.taskmanagementsystem.dto.CommentDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateCommentDTO;
import com.melnikov.taskmanagementsystem.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Operations related to comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(summary = "Get all comments", description = "Retrieve a paginated list of all comments. FOR ADMIN ONLY.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CommentDTO>> getAllComments(Pageable pageable) {
        log.info("Fetching all comments with pageable: {}", pageable);
        Page<CommentDTO> comments = commentService.getAllComments(pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by id", description = "Retrieve a comment with provided id. FOR ADMIN AND COMMENT AUTHOR.")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id, authentication.principal.email)")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id, Authentication authentication) {
        log.info("Fetching comment by id: {}", id);
        CommentDTO comment = commentService.getCommentById(id);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            log.warn("Comment not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create a new comment", description = "Create a new comment with the provided details. FOR ADMIN AND TASK AUTHOR/ASSIGNEE.")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isTaskAuthorOrAssignee(#createCommentDTO.taskId, authentication.principal.email)")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CreateCommentDTO createCommentDTO, Authentication authentication) {
        log.info("Creating new comment with details: {}", createCommentDTO);
        CommentDTO createdComment = commentService.createComment(createCommentDTO);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment", description = "Update comment with provided id. FOR ADMIN AND COMMENT AUTHOR.")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id, authentication.principal.email)")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody CommentDTO commentDTO, Authentication authentication) {
        log.info("Updating comment with id: {} and details: {}", id, commentDTO);
        CommentDTO updatedComment = commentService.updateComment(id, commentDTO);
        if (updatedComment != null) {
            return ResponseEntity.ok(updatedComment);
        } else {
            log.warn("Comment not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete comment with provided id. FOR ADMIN AND COMMENT AUTHOR.")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#id, authentication.principal.email)")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        log.info("Deleting comment with id: {}", id);
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all comments for a specific task", description = "Retrieve a paginated list of all comments related to a task with provided id. FOR ADMIN AND TASK AUTHOR/ASSIGNEE.")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isTaskAuthorOrAssignee(#taskId, authentication.principal.email)")
    public ResponseEntity<Page<CommentDTO>> getCommentsByTaskId(@PathVariable Long taskId, Pageable pageable, Authentication authentication) {
        log.info("Fetching comments for task with id: {} and pageable: {}", taskId, pageable);
        Page<CommentDTO> comments = commentService.getCommentsByTaskId(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}
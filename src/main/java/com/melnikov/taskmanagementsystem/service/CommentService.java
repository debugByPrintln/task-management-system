package com.melnikov.taskmanagementsystem.service;

import com.melnikov.taskmanagementsystem.dto.CommentDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateCommentDTO;
import com.melnikov.taskmanagementsystem.exception.task.AuthorNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.TaskNotFoundException;
import com.melnikov.taskmanagementsystem.exception.comment.CommentNotFoundException;
import com.melnikov.taskmanagementsystem.model.Comment;
import com.melnikov.taskmanagementsystem.model.Task;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.repository.CommentRepository;
import com.melnikov.taskmanagementsystem.repository.TaskRepository;
import com.melnikov.taskmanagementsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<CommentDTO> getAllComments(Pageable pageable) {
        log.info("Fetching all comments with pageable: {}", pageable);
        return commentRepository.findAll(pageable).map(this::convertToDTO);
    }

    public CommentDTO getCommentById(Long id) {
        log.info("Fetching comment by id: {}", id);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found with id: {}", id);
                    return new CommentNotFoundException("Comment not found with id: " + id);
                });
        return convertToDTO(comment);
    }

    public CommentDTO createComment(CreateCommentDTO createCommentDTO) {
        log.info("Creating new comment with details: {}", createCommentDTO);
        Comment comment = convertCreateToEntity(createCommentDTO);
        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        log.info("Updating comment with id: {} and details: {}", id, commentDTO);
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found with id: {}", id);
                    return new CommentNotFoundException("Comment not found with id: " + id);
                });
        existingComment.setText(commentDTO.getText());
        Comment updatedComment = commentRepository.save(existingComment);
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Long id) {
        log.info("Deleting comment with id: {}", id);
        if (!commentRepository.existsById(id)) {
            log.warn("Comment not found with id: {}", id);
            throw new CommentNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    public Page<CommentDTO> getCommentsByTaskId(Long taskId, Pageable pageable) {
        log.info("Fetching comments for task with id: {} and pageable: {}", taskId, pageable);
        return commentRepository.findByTaskId(taskId, pageable).map(this::convertToDTO);
    }

    public boolean isCommentAuthor(Long commentId, String email) {
        log.info("Checking if user with email: {} is the author of comment with id: {}", email, commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found with id: {}", commentId);
                    return new CommentNotFoundException("Comment not found with id: " + commentId);
                });
        User author = comment.getAuthor();
        return author != null && author.getEmail().equals(email);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setTaskId(comment.getTask().getId());
        commentDTO.setAuthorId(comment.getAuthor().getId());
        return commentDTO;
    }

    private Comment convertCreateToEntity(CreateCommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());

        Task task = taskRepository.findById(commentDTO.getTaskId())
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", commentDTO.getTaskId());
                    return new TaskNotFoundException("Task not found with id: " + commentDTO.getTaskId());
                });
        comment.setTask(task);

        User author = userRepository.findById(commentDTO.getAuthorId())
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", commentDTO.getAuthorId());
                    return new AuthorNotFoundException("Author not found with id: " + commentDTO.getAuthorId());
                });
        comment.setAuthor(author);

        return comment;
    }
}
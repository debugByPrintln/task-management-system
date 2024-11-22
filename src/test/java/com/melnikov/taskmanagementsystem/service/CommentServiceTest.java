package com.melnikov.taskmanagementsystem.service;

import com.melnikov.taskmanagementsystem.dto.CommentDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateCommentDTO;
import com.melnikov.taskmanagementsystem.exception.comment.CommentNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.AuthorNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.TaskNotFoundException;
import com.melnikov.taskmanagementsystem.model.Comment;
import com.melnikov.taskmanagementsystem.model.Task;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.repository.CommentRepository;
import com.melnikov.taskmanagementsystem.repository.TaskRepository;
import com.melnikov.taskmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User author;
    private Task task;
    private Comment comment;
    private CommentDTO commentDTO;

    private CreateCommentDTO createCommentDTO;

    @BeforeEach
    public void setUp() {
        author = new User();
        author.setId(1L);
        author.setEmail("author@example.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a test comment");
        comment.setTask(task);
        comment.setAuthor(author);

        commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setText("This is a test comment");
        commentDTO.setTaskId(1L);
        commentDTO.setAuthorId(1L);

        createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setText("This is a test comment");
        createCommentDTO.setTaskId(1L);
        createCommentDTO.setAuthorId(1L);
    }

    @Test
    public void testGetAllComments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(Arrays.asList(comment), pageable, 1);
        when(commentRepository.findAll(pageable)).thenReturn(commentPage);

        Page<CommentDTO> comments = commentService.getAllComments(pageable);
        assertEquals(1, comments.getContent().size());
        assertEquals("This is a test comment", comments.getContent().get(0).getText());
    }

    @Test
    public void testGetCommentById() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        CommentDTO foundComment = commentService.getCommentById(1L);
        assertNotNull(foundComment);
        assertEquals("This is a test comment", foundComment.getText());
    }

    @Test
    public void testGetCommentByIdNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    public void testCreateComment() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDTO createdComment = commentService.createComment(createCommentDTO);
        assertNotNull(createdComment);
        assertEquals("This is a test comment", createdComment.getText());
    }

    @Test
    public void testCreateCommentWithNonExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> commentService.createComment(createCommentDTO));
    }

    @Test
    public void testCreateCommentWithNonExistingAuthor() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AuthorNotFoundException.class, () -> commentService.createComment(createCommentDTO));
    }

    @Test
    public void testUpdateComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDTO updatedComment = commentService.updateComment(1L, commentDTO);
        assertNotNull(updatedComment);
        assertEquals("This is a test comment", updatedComment.getText());
    }

    @Test
    public void testUpdateCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(1L, commentDTO));
    }

    @Test
    public void testDeleteComment() {
        when(commentRepository.existsById(1L)).thenReturn(true);
        commentService.deleteComment(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteCommentNotFound() {
        when(commentRepository.existsById(1L)).thenReturn(false);
        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(1L));
    }

    @Test
    public void testGetCommentsByTaskId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(Arrays.asList(comment), pageable, 1);
        when(commentRepository.findByTaskId(1L, pageable)).thenReturn(commentPage);

        Page<CommentDTO> comments = commentService.getCommentsByTaskId(1L, pageable);
        assertEquals(1, comments.getContent().size());
        assertEquals("This is a test comment", comments.getContent().get(0).getText());
    }

    @Test
    public void testGetCommentsByTaskIdNoComments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        when(commentRepository.findByTaskId(1L, pageable)).thenReturn(commentPage);

        Page<CommentDTO> comments = commentService.getCommentsByTaskId(1L, pageable);
        assertTrue(comments.getContent().isEmpty());
    }
}
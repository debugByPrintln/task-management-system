package com.melnikov.taskmanagementsystem.repository;

import com.melnikov.taskmanagementsystem.model.Comment;
import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.Task;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.Priority;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import com.melnikov.taskmanagementsystem.model.utils.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User author;
    private Task task;
    private Comment comment;
    private Role userRole;

    @BeforeEach
    public void setUp() {
        userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        roleRepository.save(userRole);

        author = new User();
        author.setEmail("author@example.com");
        author.setPassword("password");
        author.setRole(userRole);
        userRepository.save(author);

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(Status.PENDING);
        task.setPriority(Priority.MEDIUM);
        task.setAuthor(author);
        taskRepository.save(task);

        comment = new Comment();
        comment.setText("This is a test comment");
        comment.setTask(task);
        comment.setAuthor(author);
        commentRepository.save(comment);
    }

    @Test
    public void testFindByTaskId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> comments = commentRepository.findByTaskId(task.getId(), pageable);
        assertFalse(comments.isEmpty());
        assertEquals(1, comments.getContent().size());
        assertEquals("This is a test comment", comments.getContent().get(0).getText());
    }

    @Test
    public void testFindByTaskIdNoComments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> comments = commentRepository.findByTaskId(999L, pageable);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testSaveComment() {
        Comment newComment = new Comment();
        newComment.setText("This is a new comment");
        newComment.setTask(task);
        newComment.setAuthor(author);
        Comment savedComment = commentRepository.save(newComment);

        assertNotNull(savedComment.getId());
        assertEquals("This is a new comment", savedComment.getText());
    }

    @Test
    public void testDeleteComment() {
        commentRepository.delete(comment);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> comments = commentRepository.findByTaskId(task.getId(), pageable);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testPagination() {
        for (int i = 0; i < 25; i++) {
            Comment newComment = new Comment();
            newComment.setText("Comment " + i);
            newComment.setTask(task);
            newComment.setAuthor(author);
            commentRepository.save(newComment);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> comments = commentRepository.findByTaskId(task.getId(), pageable);
        assertEquals(10, comments.getContent().size());
        assertEquals(26, comments.getTotalElements()); // 25 + 1 initial comment
    }
}
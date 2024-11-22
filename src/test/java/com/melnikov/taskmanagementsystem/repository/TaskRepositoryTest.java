package com.melnikov.taskmanagementsystem.repository;

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
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User author;
    private User assignee;
    private Task task;
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

        assignee = new User();
        assignee.setEmail("assignee@example.com");
        assignee.setPassword("password");
        assignee.setRole(userRole);
        userRepository.save(assignee);

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(Status.PENDING);
        task.setPriority(Priority.MEDIUM);
        task.setAuthor(author);
        task.setAssignee(assignee);
        taskRepository.save(task);
    }

    @Test
    public void testFindByAuthorId() {
        Page<Task> tasks = taskRepository.findByAuthorId(author.getId(), PageRequest.of(0, 10));
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByAuthorIdNoTasks() {
        Page<Task> tasks = taskRepository.findByAuthorId(999L, PageRequest.of(0, 10));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testFindByAssigneeId() {
        Page<Task> tasks = taskRepository.findByAssigneeId(assignee.getId(), PageRequest.of(0, 10));
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByAssigneeIdNoTasks() {
        Page<Task> tasks = taskRepository.findByAssigneeId(999L, PageRequest.of(0, 10));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testSaveTask() {
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("This is a new task");
        newTask.setStatus(Status.IN_PROGRESS);
        newTask.setPriority(Priority.HIGH);
        newTask.setAuthor(author);
        newTask.setAssignee(assignee);
        Task savedTask = taskRepository.save(newTask);

        assertNotNull(savedTask.getId());
        assertEquals("New Task", savedTask.getTitle());
    }

    @Test
    public void testDeleteTask() {
        taskRepository.delete(task);
        Page<Task> tasks = taskRepository.findByAuthorId(author.getId(), PageRequest.of(0, 10));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testPagination() {
        for (int i = 0; i < 25; i++) {
            Task newTask = new Task();
            newTask.setTitle("Task " + i);
            newTask.setDescription("This is task " + i);
            newTask.setStatus(Status.PENDING);
            newTask.setPriority(Priority.MEDIUM);
            newTask.setAuthor(author);
            newTask.setAssignee(assignee);
            taskRepository.save(newTask);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasks = taskRepository.findByAuthorId(author.getId(), pageable);
        assertEquals(10, tasks.getContent().size());
        assertEquals(26, tasks.getTotalElements()); // 25 + 1 initial task
    }
}
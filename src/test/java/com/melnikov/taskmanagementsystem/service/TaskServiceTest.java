package com.melnikov.taskmanagementsystem.service;

import com.melnikov.taskmanagementsystem.dto.TaskDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateTaskDTO;
import com.melnikov.taskmanagementsystem.exception.task.AssigneeNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.AuthorNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.TaskNotFoundException;
import com.melnikov.taskmanagementsystem.model.Task;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.Priority;
import com.melnikov.taskmanagementsystem.model.utils.Status;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User author;
    private User assignee;
    private Task task;
    private TaskDTO taskDTO;

    private CreateTaskDTO createTaskDTO;

    @BeforeEach
    public void setUp() {
        author = new User();
        author.setId(1L);
        author.setEmail("author@example.com");

        assignee = new User();
        assignee.setId(2L);
        assignee.setEmail("assignee@example.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(Status.PENDING);
        task.setPriority(Priority.MEDIUM);
        task.setAuthor(author);
        task.setAssignee(assignee);

        taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("This is a test task");
        taskDTO.setStatus(Status.PENDING);
        taskDTO.setPriority(Priority.MEDIUM);
        taskDTO.setAuthorId(1L);
        taskDTO.setAssigneeId(2L);

        createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("Test Task");
        createTaskDTO.setDescription("This is a test task");
        createTaskDTO.setStatus(Status.PENDING);
        createTaskDTO.setPriority(Priority.MEDIUM);
        createTaskDTO.setAuthorId(1L);
        createTaskDTO.setAssigneeId(2L);
    }

    @Test
    public void testGetAllTasks() {
        List<Task> tasks = Arrays.asList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getAllTasks(PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
    }

    @Test
    public void testGetTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        TaskDTO foundTask = taskService.getTaskById(1L);
        assertNotNull(foundTask);
        assertEquals("Test Task", foundTask.getTitle());
    }

    @Test
    public void testGetTaskByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    public void testCreateTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        TaskDTO createdTask = taskService.createTask(createTaskDTO);
        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
    }

    @Test
    public void testCreateTaskWithNonExistingAuthor() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AuthorNotFoundException.class, () -> taskService.createTask(createTaskDTO));
    }

    @Test
    public void testCreateTaskWithNonExistingAssignee() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> taskService.createTask(createTaskDTO));
    }

    @Test
    public void testUpdateTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        TaskDTO updatedTask = taskService.updateTask(1L, taskDTO);
        assertNotNull(updatedTask);
        assertEquals("Test Task", updatedTask.getTitle());
    }

    @Test
    public void testUpdateTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, taskDTO));
    }

    @Test
    public void testUpdateTaskWithNonExistingAssignee() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(AssigneeNotFoundException.class, () -> taskService.updateTask(1L, taskDTO));
    }

    @Test
    public void testDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        taskService.deleteTask(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteTaskNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
    }

    @Test
    public void testGetTasksByAuthorId() {
        List<Task> tasks = Arrays.asList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskRepository.findByAuthorId(eq(1L), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getTasksByAuthorId(1L, PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
    }

    @Test
    public void testGetTasksByAuthorIdNoTasks() {
        List<Task> tasks = Arrays.asList();
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskRepository.findByAuthorId(eq(1L), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getTasksByAuthorId(1L, PageRequest.of(0, 10));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetTasksByAssigneeId() {
        List<Task> tasks = Arrays.asList(task);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskRepository.findByAssigneeId(eq(2L), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getTasksByAssigneeId(2L, PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().get(0).getTitle());
    }

    @Test
    public void testGetTasksByAssigneeIdNoTasks() {
        List<Task> tasks = Arrays.asList();
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskRepository.findByAssigneeId(eq(2L), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getTasksByAssigneeId(2L, PageRequest.of(0, 10));
        assertTrue(result.isEmpty());
    }
}
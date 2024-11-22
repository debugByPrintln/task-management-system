package com.melnikov.taskmanagementsystem.controller;

import com.melnikov.taskmanagementsystem.dto.TaskDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateTaskDTO;
import com.melnikov.taskmanagementsystem.service.TaskService;
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
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Operations related to tasks")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve a paginated list of all tasks. FOR ADMIN AND USER.")
    public ResponseEntity<Page<TaskDTO>> getAllTasks(Pageable pageable) {
        log.info("Fetching all tasks with pageable: {}", pageable);
        Page<TaskDTO> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a task by its ID. FOR ADMIN AND USER.")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        log.info("Fetching task by id: {}", id);
        TaskDTO task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            log.warn("Task not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new task", description = "Create a new task with the provided details. FOR ADMIN ONLY.")
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskDTO createTaskDTO) {
        log.info("Creating new task with details: {}", createTaskDTO);
        TaskDTO createdTask = taskService.createTask(createTaskDTO);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a task", description = "Update an existing task by its ID. FOR ADMIN ONLY.")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        log.info("Updating task with id: {} and details: {}", id, taskDTO);
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            log.warn("Task not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a task", description = "Delete a task by its ID. FOR ADMIN ONLY.")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Deleting task with id: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get tasks by author ID", description = "Retrieve a paginated list of tasks created by a specific author. FOR ADMIN AND USER.")
    public ResponseEntity<Page<TaskDTO>> getTasksByAuthorId(@PathVariable Long authorId, Pageable pageable) {
        log.info("Fetching tasks by author id: {} and pageable: {}", authorId, pageable);
        Page<TaskDTO> tasks = taskService.getTasksByAuthorId(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get tasks by assignee ID", description = "Retrieve a paginated list of tasks assigned to a specific user. FOR ADMIN AND USER.")
    public ResponseEntity<Page<TaskDTO>> getTasksByAssigneeId(@PathVariable Long assigneeId, Pageable pageable) {
        log.info("Fetching tasks by assignee id: {} and pageable: {}", assigneeId, pageable);
        Page<TaskDTO> tasks = taskService.getTasksByAssigneeId(assigneeId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isTaskAssignee(#id, authentication.principal.email)")
    @Operation(summary = "Update task status", description = "Update the status of an existing task by its ID. FOR ADMIN AND TASK ASSIGNEE.")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestBody TaskDTO taskDTO, Authentication authentication) {
        log.info("Updating task status with id: {} and status: {}", id, taskDTO.getStatus());
        TaskDTO updatedTask = taskService.updateTaskStatus(id, taskDTO.getStatus());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            log.warn("Task not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update task priority", description = "Update the priority of an existing task by its ID. FOR ADMIN ONLY.")
    public ResponseEntity<TaskDTO> updateTaskPriority(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        log.info("Updating task priority with id: {} and priority: {}", id, taskDTO.getPriority());
        TaskDTO updatedTask = taskService.updateTaskPriority(id, taskDTO.getPriority());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            log.warn("Task not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/assignee")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update task assignee", description = "Update the assignee of an existing task by its ID. FOR ADMIN ONLY.")
    public ResponseEntity<TaskDTO> updateTaskAssignee(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        log.info("Updating task assignee with id: {} and assignee id: {}", id, taskDTO.getAssigneeId());
        TaskDTO updatedTask = taskService.updateTaskAssignee(id, taskDTO.getAssigneeId());
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            log.warn("Task not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
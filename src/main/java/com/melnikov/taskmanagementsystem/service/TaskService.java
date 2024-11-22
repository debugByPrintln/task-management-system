package com.melnikov.taskmanagementsystem.service;

import com.melnikov.taskmanagementsystem.dto.CommentDTO;
import com.melnikov.taskmanagementsystem.dto.TaskDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateTaskDTO;
import com.melnikov.taskmanagementsystem.exception.task.AuthorNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.AssigneeNotFoundException;
import com.melnikov.taskmanagementsystem.exception.task.TaskNotFoundException;
import com.melnikov.taskmanagementsystem.model.Comment;
import com.melnikov.taskmanagementsystem.model.Task;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.Priority;
import com.melnikov.taskmanagementsystem.model.utils.Status;
import com.melnikov.taskmanagementsystem.repository.TaskRepository;
import com.melnikov.taskmanagementsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        log.info("Fetching all tasks with pageable: {}", pageable);
        return taskRepository.findAll(pageable).map(this::convertToDTO);
    }

    public TaskDTO getTaskById(Long id){
        log.info("Fetching task by id: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        TaskDTO taskDTO = convertToDTO(task);
        taskDTO.setComments(task.getComments().stream().map(this::convertCommentToDTO).collect(Collectors.toList()));
        return taskDTO;
    }

    public TaskDTO createTask(CreateTaskDTO createTaskDTO) {
        log.info("Creating new task with details: {}", createTaskDTO);
        Task task = convertCreateToEntity(createTaskDTO);
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        log.info("Updating task with id: {} and details: {}", id, taskDTO);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setStatus(taskDTO.getStatus());
        existingTask.setPriority(taskDTO.getPriority());
        User assignee = userRepository.findById(taskDTO.getAssigneeId())
                .orElseThrow(() -> {
                    log.warn("Assignee not found with id: {}", taskDTO.getAssigneeId());
                    return new AssigneeNotFoundException("Assignee not found with id: " + taskDTO.getAssigneeId());
                });
        existingTask.setAssignee(assignee);
        Task updatedTask = taskRepository.save(existingTask);
        return convertToDTO(updatedTask);
    }

    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        if (!taskRepository.existsById(id)) {
            log.warn("Task not found with id: {}", id);
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public Page<TaskDTO> getTasksByAuthorId(Long authorId, Pageable pageable) {
        log.info("Fetching tasks by author id: {} and pageable: {}", authorId, pageable);
        return taskRepository.findByAuthorId(authorId, pageable).map(this::convertToDTO);
    }

    public Page<TaskDTO> getTasksByAssigneeId(Long assigneeId, Pageable pageable) {
        log.info("Fetching tasks by assignee id: {} and pageable: {}", assigneeId, pageable);
        return taskRepository.findByAssigneeId(assigneeId, pageable).map(this::convertToDTO);
    }

    public boolean isTaskAssignee(Long taskId, String email) {
        log.info("Checking if user with email: {} is the assignee of task with id: {}", email, taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            User assignee = task.get().getAssignee();
            return assignee != null && assignee.getEmail().equals(email);
        }
        return false;
    }

    public boolean isTaskAuthorOrAssignee(Long taskId, String email) {
        log.info("Checking if user with email: {} is the author or assignee of task with id: {}", email, taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            User author = task.get().getAuthor();
            User assignee = task.get().getAssignee();
            return (author != null && author.getEmail().equals(email)) || (assignee != null && assignee.getEmail().equals(email));
        }
        return false;
    }

    public TaskDTO updateTaskStatus(Long id, Status status) {
        log.info("Updating task status with id: {} and status: {}", id, status);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        existingTask.setStatus(status);
        Task updatedTask = taskRepository.save(existingTask);
        return convertToDTO(updatedTask);
    }

    public TaskDTO updateTaskPriority(Long id, Priority priority) {
        log.info("Updating task priority with id: {} and priority: {}", id, priority);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        existingTask.setPriority(priority);
        Task updatedTask = taskRepository.save(existingTask);
        return convertToDTO(updatedTask);
    }

    public TaskDTO updateTaskAssignee(Long id, Long assigneeId) {
        log.info("Updating task assignee with id: {} and assignee id: {}", id, assigneeId);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new TaskNotFoundException("Task not found with id: " + id);
                });
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> {
                    log.warn("Assignee not found with id: {}", assigneeId);
                    return new AssigneeNotFoundException("Assignee not found with id: " + assigneeId);
                });
        existingTask.setAssignee(assignee);
        Task updatedTask = taskRepository.save(existingTask);
        return convertToDTO(updatedTask);
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setAuthorId(task.getAuthor().getId());
        taskDTO.setAssigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null);
        taskDTO.setComments(task.getComments().stream().map(this::convertCommentToDTO).collect(Collectors.toList()));
        return taskDTO;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setTaskId(comment.getTask().getId());
        commentDTO.setAuthorId(comment.getAuthor().getId());
        return commentDTO;
    }

    private Task convertCreateToEntity(CreateTaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        User author = userRepository.findById(taskDTO.getAuthorId())
                .orElseThrow(() -> {
                    log.warn("Author not found with id: {}", taskDTO.getAuthorId());
                    return new AuthorNotFoundException("Author not found with id: " + taskDTO.getAuthorId());
                });
        task.setAuthor(author);
        task.setAssignee(userRepository.findById(taskDTO.getAssigneeId()).orElse(null));
        return task;
    }
}
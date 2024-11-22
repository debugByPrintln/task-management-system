package com.melnikov.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.taskmanagementsystem.dto.TaskDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateTaskDTO;
import com.melnikov.taskmanagementsystem.model.utils.Priority;
import com.melnikov.taskmanagementsystem.model.utils.Status;
import com.melnikov.taskmanagementsystem.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO taskDTO;

    private CreateTaskDTO createTaskDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
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
    public void testGetAllTasks() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(taskDTO);
        Page<TaskDTO> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskService.getAllTasks(any(PageRequest.class))).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }

    @Test
    public void testGetTaskById() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskDTO);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }


    @Test
    public void testCreateTask() throws Exception {
        when(taskService.createTask(any(CreateTaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }


    @Test
    public void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testGetTasksByAuthorId() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(taskDTO);
        Page<TaskDTO> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskService.getTasksByAuthorId(eq(1L), any(PageRequest.class))).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks/author/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }

    @Test
    public void testGetTasksByAssigneeId() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(taskDTO);
        Page<TaskDTO> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), tasks.size());
        when(taskService.getTasksByAssigneeId(eq(2L), any(PageRequest.class))).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks/assignee/2")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }
}
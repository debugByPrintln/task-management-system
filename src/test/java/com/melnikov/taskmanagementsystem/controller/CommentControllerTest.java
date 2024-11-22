package com.melnikov.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.taskmanagementsystem.dto.CommentDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateCommentDTO;
import com.melnikov.taskmanagementsystem.service.CommentService;
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
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentDTO commentDTO;

    private CreateCommentDTO createCommentDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

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
    public void testGetAllComments() throws Exception {
        List<CommentDTO> comments = Arrays.asList(commentDTO);
        Page<CommentDTO> commentPage = new PageImpl<>(comments, PageRequest.of(0, 10), comments.size());
        when(commentService.getAllComments(any(PageRequest.class))).thenReturn(commentPage);

        mockMvc.perform(get("/api/comments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].text").value("This is a test comment"));
    }

    @Test
    public void testGetCommentById() throws Exception {
        when(commentService.getCommentById(1L)).thenReturn(commentDTO);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("This is a test comment"));
    }

    @Test
    public void testCreateComment() throws Exception {
        when(commentService.createComment(any(CreateCommentDTO.class))).thenReturn(commentDTO);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createCommentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("This is a test comment"));
    }

    @Test
    public void testUpdateComment() throws Exception {
        when(commentService.updateComment(eq(1L), any(CommentDTO.class))).thenReturn(commentDTO);

        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("This is a test comment"));
    }


    @Test
    public void testDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testGetCommentsByTaskId() throws Exception {
        List<CommentDTO> comments = Arrays.asList(commentDTO);
        Page<CommentDTO> commentPage = new PageImpl<>(comments, PageRequest.of(0, 10), comments.size());
        when(commentService.getCommentsByTaskId(eq(1L), any(PageRequest.class))).thenReturn(commentPage);

        mockMvc.perform(get("/api/comments/task/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].text").value("This is a test comment"));
    }

    @Test
    public void testGetCommentsByTaskIdNoComments() throws Exception {
        List<CommentDTO> comments = Arrays.asList();
        Page<CommentDTO> commentPage = new PageImpl<>(comments, PageRequest.of(0, 10), comments.size());
        when(commentService.getCommentsByTaskId(eq(1L), any(PageRequest.class))).thenReturn(commentPage);

        mockMvc.perform(get("/api/comments/task/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
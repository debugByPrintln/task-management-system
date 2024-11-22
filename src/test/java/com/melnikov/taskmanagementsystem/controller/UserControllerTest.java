package com.melnikov.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.taskmanagementsystem.dto.UserDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateUserDTO;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import com.melnikov.taskmanagementsystem.service.UserService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;

    private CreateUserDTO createUserDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setPassword("password");
        userDTO.setRole(RoleName.ROLE_USER);

        createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("user@example.com");
        createUserDTO.setPassword("password");
        createUserDTO.setRole(RoleName.ROLE_USER);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(userDTO);
        Page<UserDTO> userPage = new PageImpl<>(users, PageRequest.of(0, 10), users.size());
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(userPage);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].email").value("user@example.com"));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createUserDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("user@example.com")).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/email/user@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }
}
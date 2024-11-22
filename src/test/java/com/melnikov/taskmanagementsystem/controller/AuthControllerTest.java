package com.melnikov.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melnikov.taskmanagementsystem.dto.UserDTO;
import com.melnikov.taskmanagementsystem.dto.auth.AuthRequestDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateUserDTO;
import com.melnikov.taskmanagementsystem.jwt.JwtTokenProvider;
import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import com.melnikov.taskmanagementsystem.service.CustomUserDetailsService;
import com.melnikov.taskmanagementsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private AuthRequestDTO authRequestDTO;
    private CreateUserDTO createUserDTO;
    private User user;

    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("user@example.com");
        authRequestDTO.setPassword("password");

        createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("user@example.com");
        createUserDTO.setPassword("password");
        createUserDTO.setRole(RoleName.ROLE_USER);

        userDTO = new UserDTO();
        userDTO.setEmail("user@example.com");
        userDTO.setPassword("password");
        userDTO.setRole(RoleName.ROLE_USER);

        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(role);
    }

    @Test
    public void testSignIn() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtTokenProvider.createToken("user@example.com", RoleName.ROLE_USER.name())).thenReturn("jwtToken");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    public void testSignUp() throws Exception {
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(userDTO);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtTokenProvider.createToken("user@example.com", RoleName.ROLE_USER.name())).thenReturn("jwtToken");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createUserDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }
}

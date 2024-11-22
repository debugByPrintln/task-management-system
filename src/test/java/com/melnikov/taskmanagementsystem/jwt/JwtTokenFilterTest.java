package com.melnikov.taskmanagementsystem.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class JwtTokenFilterTest {

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoFilterValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "validToken";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.resolveToken(request)).thenReturn(token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(mock(Authentication.class));

        jwtTokenFilter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "invalidToken";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.resolveToken(request)).thenReturn(token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        jwtTokenFilter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);

        verify(filterChain).doFilter(request, response);
    }
}

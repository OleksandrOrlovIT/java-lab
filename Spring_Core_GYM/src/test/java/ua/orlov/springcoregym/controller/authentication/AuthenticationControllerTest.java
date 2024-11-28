package ua.orlov.springcoregym.controller.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.orlov.springcoregym.service.security.AuthenticationService;
import ua.orlov.springcoregym.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authenticationController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void loginThenSuccess() throws Exception {
        when(authenticationService.login(any(), any())).thenReturn("");

        MvcResult result = mockMvc.perform(post("/api/v1/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertEquals("{\"token\":\"\"}", responseBody);
    }

    @Test
    void changeLoginThenSuccess() throws Exception {
        when(userService.changeUserPassword(any(), any(), any())).thenReturn(true);

        MvcResult result = mockMvc.perform(put("/api/v1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\", \"oldPassword\":\"password\", \"newPassword\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertEquals("\"You successfully changed password\"", responseBody);
    }


    @Test
    void changeLoginThenFailure() throws Exception {
        when(userService.changeUserPassword(any(), any(), any())).thenReturn(false);

        MvcResult result = mockMvc.perform(put("/api/v1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\", \"oldPassword\":\"password\", \"newPassword\":\"password\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertEquals("\"Password hasn't been changed\"", responseBody);
    }

    @Test
    void logoutThenSuccess() throws Exception {
        String mockToken = "Bearer mock_jwt_token";

        MvcResult result = mockMvc.perform(post("/api/v1/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", mockToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        assertEquals("\"Logged out successfully.\"", responseBody);
        verify(authenticationService, times(1)).logout(any());
    }

}

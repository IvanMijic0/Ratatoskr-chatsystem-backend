package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.userDtos.UsersResponse;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.UserService;
import ba.nosite.chatsystem.rest.configurations.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnListOfUsers() throws Exception {
        // Mocking the service to return a list with one user for simplicity
        User mockUser = new User(/* user details */);
        UsersResponse mockUsersResponse = new UsersResponse(mockUser);
        when(userService.list()).thenReturn(Collections.singletonList(mockUsersResponse));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(mockUsersResponse.get_id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(mockUsersResponse.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(mockUsersResponse.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").value(mockUsersResponse.getCreatedAt().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].updatedAt").value(mockUsersResponse.getUpdatedAt().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].role").value(mockUsersResponse.getRole()));
    }

//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void shouldCreateUser() throws Exception {
//        User newUser = new User(/* user details */);
//        UsersResponse createdUserResponse = new UsersResponse(newUser);
//        when(userService.save(any(User.class))).thenReturn(newUser);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/v1/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(/* JSON content for new user */))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(createdUserResponse.get_id()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(createdUserResponse.getUsername()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(createdUserResponse.getEmail()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").value(createdUserResponse.getCreatedAt().toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.updatedAt").value(createdUserResponse.getUpdatedAt().toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(createdUserResponse.getRole()));
//    }
}
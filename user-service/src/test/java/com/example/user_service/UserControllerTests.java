package com.example.user_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.user_service.models.User;
import com.example.user_service.dtos.UserDto;
import com.example.user_service.dtos.UserDTO1;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setRole("USER");
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        List<User> allUsers = Arrays.asList(testUser);
        when(userService.getAll()).thenReturn(allUsers);

        mockMvc.perform(get("/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getUserById_WhenUserDoesNotExist_ShouldReturn404() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/1")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void createUser_WhenUsernameIsUnique_ShouldCreateUser() throws Exception {
        when(userService.getUserByUsername(any())).thenReturn(null);
        when(userService.createUser(any())).thenReturn(testUser);

        mockMvc.perform(post("/users/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void createUser_WhenUsernameExists_ShouldReturn400() throws Exception {
        when(userService.getUserByUsername(any())).thenReturn(testUser);

        mockMvc.perform(post("/users/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void deleteUser_WhenUserExists_ShouldDeleteUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Korisnik je obrisan!"));
    }

    @Test
    @WithMockUser(authorities = {"admin", "SCOPE_internal"})
    void getCoursesForUser_ShouldReturnCoursesList() throws Exception {
        List<String> courses = Arrays.asList("Course1", "Course2");
        when(userService.getCoursesForUser(1L)).thenReturn(courses);

        mockMvc.perform(get("/users/1/courses")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Course1"))
                .andExpect(jsonPath("$[1]").value("Course2"));
    }

    @Test
    @WithMockUser(authorities = {"admin", "SCOPE_internal"})
    void getUsersByIds_ShouldReturnUserDtoList() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByIds(anyList())).thenReturn(users);

        mockMvc.perform(get("/users/forCourse")
                .with(csrf())
                .param("ids", "1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    @WithMockUser(authorities = {"admin", "SCOPE_internal"})
    void getUserForReview_ShouldReturnUserDto() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/forReview")
                .with(csrf())
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser(authorities = {"admin", "SCOPE_internal"})
    void getUserForNotification_ShouldReturnUserDTO1() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/forNotification")
                .with(csrf())
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
@WithMockUser(authorities = "admin")
void updateUser_WhenUserExists_ShouldUpdateUser() throws Exception {
    // Arrange
    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setEmail("updated@test.com");
    updatedUser.setUsername("updatedUser");
    updatedUser.setRole("USER");
    
    when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
    when(userService.getUserByUsername("updatedUser")).thenReturn(null);
    // Ne koristimo when za updateUser jer vraća void

    mockMvc.perform(put("/users/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isOk());
}

@Test
@WithMockUser(authorities = "admin")
void updateUser_WhenUserNotFound_ShouldReturn404() throws Exception {
    // Arrange
    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setEmail("updated@test.com");
    updatedUser.setUsername("updatedUser");
    
    when(userService.getUserById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/users/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Korisnik nije pronađen!"));
}

@Test
@WithMockUser(authorities = "admin")
void updateUser_WhenUsernameExists_ShouldReturn409() throws Exception {
    // Arrange
    User updatedUser = new User();
    updatedUser.setId(1L);
    updatedUser.setEmail("updated@test.com");
    updatedUser.setUsername("existingUser");
    
    User existingUser = new User();
    existingUser.setUsername("existingUser");

    when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
    when(userService.getUserByUsername("existingUser")).thenReturn(existingUser);

    mockMvc.perform(put("/users/1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedUser)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Korisničko ime 'existingUser' je već zauzeto!"));
}

   

@Test
void whenUnauthorizedAccess_ShouldRedirectToLogin() throws Exception {
    mockMvc.perform(get("/users")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
}



// Dodajmo još nekoliko testova za proveru autorizacije

@Test
@WithMockUser(authorities = "user")
void whenUserAccessesUserEndpoint_ShouldSucceed() throws Exception {
    when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/users/forReview")
            .with(csrf())
            .param("id", "1"))
            .andExpect(status().isOk());
}

@Test
@WithMockUser(authorities = "SCOPE_internal")
void whenInternalServiceAccessesEndpoint_ShouldSucceed() throws Exception {
    when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/users/forNotification")
            .with(csrf())
            .param("id", "1"))
            .andExpect(status().isOk());
}




@Test
@WithMockUser(authorities = {"admin", "SCOPE_internal"})
void whenAdminAccessesProtectedEndpoint_ShouldSucceed() throws Exception {
    List<User> allUsers = Arrays.asList(testUser);
    when(userService.getAll()).thenReturn(allUsers);

    mockMvc.perform(get("/users")
            .with(csrf()))
            .andExpect(status().isOk());
}
}

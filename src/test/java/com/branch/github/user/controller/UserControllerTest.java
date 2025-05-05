package com.branch.github.user.controller;

import com.branch.github.exception.GithubServerErrorException;
import com.branch.github.exception.GithubUserNotFoundException;
import com.branch.github.repo.service.Repo;
import com.branch.github.repo.service.RepoService;
import com.branch.github.user.service.User;
import com.branch.github.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private RepoService repoService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void whenUserExists_thenReturnUserWithRepos() throws Exception {
        //building mock user and repo from given example
        final String userName = "octocat";
        User mockUser = User.builder()
                .userName(userName)
                .displayName("The Octocat")
                .avatar("https://avatars3.githubusercontent.com/u/583231?v=4")
                .geoLocation("San Francisco")
                .email(null)
                .url("https://github.com/octocat")
                .createdAt(OffsetDateTime.parse("2011-01-25T18:44:36Z"))
                .build();

        Repo mockRepo = Repo.builder()
                .name("boysenberry-repo-1")
                .url("https://github.com/octocat/boysenberry-repo-1")
                .build();

        //mocking services
        when(userService.getUser(anyString())).thenReturn(mockUser);
        when(repoService.getReposForUser(anyString())).thenReturn(List.of(mockRepo));

        //creating expected json response
        String expectedJson = """
            {
              "user_name": "octocat",
              "display_name": "The Octocat",
              "avatar": "https://avatars3.githubusercontent.com/u/583231?v=4",
              "geo_location": "San Francisco",
              "email": null,
              "url": "https://github.com/octocat",
              "created_at": "2011-01-25 18:44:36",
              "repos": [
                {
                  "name": "boysenberry-repo-1",
                  "url": "https://github.com/octocat/boysenberry-repo-1"
                }
              ]
            }
        """;

        //assertion
        mockMvc.perform(get("/user/{userName}", userName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

    }


    @Test
    public void whenUserDoesNotExist_thenReturn404() throws Exception {
        String userName = "nonExistentUser";
        when(userService.getUser(anyString())).thenThrow(
                new GithubUserNotFoundException(HttpStatusCode.valueOf(404), "Not Found"));

        mockMvc.perform(get("/user/{userName}", userName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenServiceError_thenReturn500() throws Exception {
        String userName = "nonExistentUser";
        when(userService.getUser(anyString())).thenThrow(
                new GithubServerErrorException(HttpStatusCode.valueOf(500), "Service Error"));

        mockMvc.perform(get("/user/{userName}", userName).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
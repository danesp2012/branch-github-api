package com.branch.github.user.service;

import com.branch.github.client.GithubUser;
import com.branch.github.client.GithubUsersClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @MockitoBean
    private GithubUsersClient githubUsersClient;

    //using autowired and springboottest for integration testing with cache
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Test
    public void getUser_ShouldReturnConvertedUser() {
        String userName = "octocat";
        GithubUser mockRepoOctocat = GithubUser.builder()
                .url("https://github.com/octocat")
                .email(null)
                .avatarUrl("https://avatars3.githubusercontent.com/u/583231?v=4")
                .createdAt(OffsetDateTime.parse("2011-01-25T18:44:36Z"))
                .name("The Octocat")
                .location("San Francisco")
                .login("octocat")
                .build();

        when(githubUsersClient.getGithubUser(userName))
                .thenReturn(mockRepoOctocat);

        User result = userService.getUser(userName);
        User resultAfterCache = userService.getUser(userName);

        //asserting fields are mapped correctly
        assertEquals("octocat", result.getUserName());
        assertEquals("octocat", resultAfterCache.getUserName());
        assertEquals("The Octocat", result.getDisplayName());
        assertEquals("San Francisco", result.getGeoLocation());
        assertEquals("https://avatars3.githubusercontent.com/u/583231?v=4", result.getAvatar());
        assertEquals("https://github.com/octocat", result.getUrl());
        assertNull(result.getEmail());  // email was null in mock
        assertEquals(OffsetDateTime.parse("2011-01-25T18:44:36Z"), result.getCreatedAt());


        Cache cache = cacheManager.getCache("user");
        assertNotNull(cache);
        assertNotNull(cache.get(userName));
        //verifying client was only called once due to cache
        verify(githubUsersClient, times(1)).getGithubUser(userName);

    }


}
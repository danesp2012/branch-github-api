package com.branch.github.repo.service;

import com.branch.github.client.GithubRepo;
import com.branch.github.client.GithubUsersClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RepoServiceTest {

    @MockitoBean
    private GithubUsersClient githubUsersClient;

    //using autowired and springboottest for integration testing with cache
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RepoService repoService;

    @Test
    public void getReposForUser_ShouldReturnConvertedRepos() {
        String userName = "octocat";
        GithubRepo mockRepoBoysen = GithubRepo.builder()
                .name("boysenberry-repo-1")
                .url("https://github.com/octocat/boysenberry-repo-1")
                .build();
        GithubRepo mockRepoRasp = GithubRepo.builder()
                .name("raspberry-repo-1")
                .url("https://github.com/octocat/raspberry-repo-1")
                .build();

        when(githubUsersClient.getGithubReposForUser(userName))
                .thenReturn(List.of(mockRepoBoysen, mockRepoRasp));

        List<Repo> result = repoService.getReposForUser(userName);
        List<Repo> resultAfterCache = repoService.getReposForUser(userName);

        assertEquals(2, result.size());
        assertEquals(2, resultAfterCache.size());
        //asserting fields are mapped correctly
        assertEquals("boysenberry-repo-1", result.get(0).getName());
        assertEquals("https://github.com/octocat/boysenberry-repo-1", result.get(0).getUrl());
        assertEquals("raspberry-repo-1", result.get(1).getName());
        assertEquals("https://github.com/octocat/raspberry-repo-1", result.get(1).getUrl());

        Cache cache = cacheManager.getCache("repo");
        assertNotNull(cache);
        assertNotNull(cache.get(userName, List.class));
        //verifying client was only called once due to cache
        verify(githubUsersClient, times(1)).getGithubReposForUser(userName);

    }


}
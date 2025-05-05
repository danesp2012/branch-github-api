package com.branch.github.repo.service;

import com.branch.github.client.GithubRepo;
import com.branch.github.client.GithubUsersClient;
import com.branch.github.exception.GithubServerErrorException;
import com.branch.github.user.service.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

//Service to convert github user to domain user
//layer to add future business logic if needed
@Service
@AllArgsConstructor
@Slf4j
public class RepoService {

    private GithubUsersClient githubUsersClient;

    @Cacheable("repo")
    public List<Repo> getReposForUser(final String userName) {
        log.info("Fetching repos for user {}", userName);
        var githubRepos = githubUsersClient.getGithubReposForUser(userName);
        //shouldn't return nulls but throwing service error if one is returned
        if(githubRepos == null) {
            log.error("Error fetching repos for user {}", userName);
            throw new GithubServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching repos");
        }
        return githubRepos.stream()
                .map(this::convertFromGithubRepo)
                .toList();
    }

    private Repo convertFromGithubRepo(GithubRepo githubRepo) {
        return Repo.builder()
                .name(githubRepo.getName())
                .url(githubRepo.getUrl())
                .build();
    }

    @CacheEvict(value="repo", allEntries=true)
    public void evictCache() {
        log.info("Evicting repo cache");
    }

    @CachePut("repo")
    public List<Repo> updateCache(String userName) {
        log.info("Updating repo cache");
        return this.getReposForUser(userName);
    }
}

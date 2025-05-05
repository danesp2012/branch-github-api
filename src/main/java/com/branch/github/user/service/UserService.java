package com.branch.github.user.service;

import com.branch.github.client.GithubUser;
import com.branch.github.client.GithubUsersClient;
import com.branch.github.exception.GithubServerErrorException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

//Service to convert github repo to domain repo, layer to add future business logic if needed
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private GithubUsersClient githubUsersClient;

    @Cacheable("user")
    public User getUser(final String userName) {
        log.info("Fetching user info for {}", userName);
        var githubUser = githubUsersClient.getGithubUser(userName);
        if(githubUser == null) {
            log.error("Error fetching user {}", userName);
            throw new GithubServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching user");
        }
        return convertFromGithubUser(githubUser);
    }

    private User convertFromGithubUser(final GithubUser githubUser) {
        return User.builder()
                .url(githubUser.getUrl())
                .email(githubUser.getEmail())
                .avatar(githubUser.getAvatarUrl())
                .createdAt(githubUser.getCreatedAt())
                .displayName(githubUser.getName())
                .geoLocation(githubUser.getLocation())
                .userName(githubUser.getLogin())
                .build();
    }

    @CacheEvict(value="user", allEntries=true)
    public void evictCache() {
        log.info("Evicting user cache");
    }

    @CachePut("user")
    public User updateCache(final String userName) {
        log.info("Updating user cache");
        return this.getUser(userName);
    }
}

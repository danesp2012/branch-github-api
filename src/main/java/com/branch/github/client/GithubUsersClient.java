package com.branch.github.client;

import com.branch.github.exception.GithubClientErrorException;
import com.branch.github.exception.GithubServerErrorException;
import com.branch.github.exception.GithubUserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubUsersClient {
    private static final String REPOS_PATH = "repos";

    //Defining client for the Github API user resource
    //Consider adding retry policy
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.github.com/users")
            .build();

    //Consider simplifying both methods to accept a userName and a path parameter to reduce to one
    //Reasonable to do since both methods can justifiably use the same exceptions
    //and we are accessing the same base resource type
    public GithubUser getGithubUser(final String userName) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("/%s", userName))
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (request, response) -> {
                            log.error("Cannot find user {}", userName);
                            throw new GithubUserNotFoundException(response.getStatusCode(), response.getBody().toString());
                        })
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            log.error("Error finding user {}: {}", userName, response.getBody());
                            throw new GithubClientErrorException(response.getStatusCode(), response.getBody().toString());
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            log.error("Server error finding user {}: {}", userName, response.getBody());
                            throw new GithubServerErrorException(response.getStatusCode(), response.getBody().toString());
                        })
                .body(GithubUser.class);
    }

    public List<GithubRepo> getGithubReposForUser(final String userName) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("/%s/%s",userName,REPOS_PATH))
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (request, response) -> {
                            log.error("Cannot find repos for user {}", userName);
                            throw new GithubUserNotFoundException(response.getStatusCode(), response.getBody().toString());
                        })
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            log.error("Error finding repos for user {}: {}", userName, response.getBody());
                            throw new GithubClientErrorException(response.getStatusCode(), response.getBody().toString());
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            log.error("Server error finding repos for user {}: {}", userName, response.getBody());
                            throw new GithubServerErrorException(response.getStatusCode(), response.getBody().toString());
                        })
                .body(new ParameterizedTypeReference<>() {});
    }
}

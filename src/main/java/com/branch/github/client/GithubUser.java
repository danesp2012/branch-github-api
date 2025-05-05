package com.branch.github.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

//Response object for github api user
@Getter
@Setter
@Builder
public class GithubUser {
    private String login;
    private String name;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String location;
    private String email;
    private String url;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}

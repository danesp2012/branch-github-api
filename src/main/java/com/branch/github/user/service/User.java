package com.branch.github.user.service;

import com.branch.github.repo.service.Repo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

//Business defined response object/DTO
@Builder(toBuilder = true)
@Data
@Getter
public class User {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("display_name")
    private String displayName;
    private String avatar;
    @JsonProperty("geo_location")
    private String geoLocation;
    private String email;
    private String url;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime createdAt;
    private List<Repo> repos;
}


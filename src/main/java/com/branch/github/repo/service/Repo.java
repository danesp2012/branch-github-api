package com.branch.github.repo.service;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

//Significant code repetition between this DTO and the Github object
//but keeping both for extensibility and abstraction since the user object is decoupled
@Builder
@Data
@Getter
public class Repo {
    private String name;
    private String url;
}


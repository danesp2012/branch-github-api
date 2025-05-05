package com.branch.github.client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//Github response object for repository
@Getter
@Setter
@Builder
public class GithubRepo {
    private String name;
    private String url;
}

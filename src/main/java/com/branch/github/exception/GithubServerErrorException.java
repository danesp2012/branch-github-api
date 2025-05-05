package com.branch.github.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

//General custom exception for Github api error handling
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GithubServerErrorException extends HttpClientErrorException {

    public GithubServerErrorException(HttpStatusCode statusCode, String statusText) {
        super(statusCode, statusText);
    }

}

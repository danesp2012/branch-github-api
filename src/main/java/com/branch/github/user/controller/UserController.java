package com.branch.github.user.controller;

import com.branch.github.repo.service.RepoService;
import com.branch.github.user.service.User;
import com.branch.github.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private RepoService repoService;


    //couldn't find a definitive source on max login length for GitHub but the signup forms appears to limit it to 39
    @Operation(summary = "Get user info by user name",
            description = "Returns Github user and repository info for user name")
    @GetMapping("/{userName}")
    private User getUser(@PathVariable @Size(max = 39,message = "Username must be less than 39 characters")
                             final String userName) {
        //Merge user and repo domain objects for response
        return userService.getUser(userName).toBuilder()
                .repos(repoService.getReposForUser(userName))
                .build();
    }

    //Would make sense to break repo cache management to its own controller,
    // repo and user cache would probably update at different rates
    // but keeping them together for simplicity
    @Operation(summary = "Evict User cache",
            description = "Evicts cache for user info and their repos")
    @DeleteMapping("/cache")
    private void evictUserCache() {
        repoService.evictCache();
        userService.evictCache();
    }

    @Operation(summary = "Update User cache",
            description = "Updates cache for user info and their repos")
    @PutMapping("/cache/{userName}")
    private void updateUserCache(@PathVariable final String userName) {
        repoService.updateCache(userName);
        userService.updateCache(userName);
    }

}

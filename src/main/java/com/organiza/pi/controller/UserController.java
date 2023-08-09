package com.organiza.pi.controller;

import com.organiza.pi.model.User;
import com.organiza.pi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class UserController {

    public UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(description = "getAllUsers", summary = "getAllUsers")
    public CompletableFuture<List<User>> getAllUsers() {
        return userService.getAll();
    }


    @GetMapping("/{id}")
    @Operation(description = "getUsersbyId", summary = "getUsersbyId")
    public ResponseEntity<User> getById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Optional<User> user = userService.getById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "createUser", summary = "createUser")
    public ResponseEntity<User> createUser(@RequestBody User user, HttpServletResponse response) throws ExecutionException, InterruptedException {
        User savedUser = userService.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        response.setHeader("Location", uri.toASCIIString());

        return ResponseEntity.created(uri).body(savedUser);
    }
}

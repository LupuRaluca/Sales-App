package com.sia.salesapp.web.controller;

import com.sia.salesapp.application.iServices.UserService;
import com.sia.salesapp.web.dto.UserRequest;
import com.sia.salesapp.web.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody @Valid UserRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
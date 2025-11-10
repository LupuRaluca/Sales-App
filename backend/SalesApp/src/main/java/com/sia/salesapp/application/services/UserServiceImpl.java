package com.sia.salesapp.application.services;


import com.sia.salesapp.application.iServices.UserService;
import com.sia.salesapp.domain.entity.User;
import com.sia.salesapp.infrastructure.repository.UserRepository;
import com.sia.salesapp.web.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;

    @Override @Transactional
    public UserResponse create(UserRequest req) {
        if (repo.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException("Email deja existent");
        if (repo.existsByUsernameIgnoreCase(req.username()))
            throw new IllegalArgumentException("Username deja existent");

        User u = repo.save(User.builder()
                .username(req.username())
                .email(req.email())
                .password(req.password())
                .build());
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    @Override @Transactional
    public UserResponse update(Long id, UserRequest req) {
        User u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User inexistent"));

        if (!u.getEmail().equalsIgnoreCase(req.email()) && repo.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException("Email deja existent");
        if (!u.getUsername().equalsIgnoreCase(req.username()) && repo.existsByUsernameIgnoreCase(req.username()))
            throw new IllegalArgumentException("Username deja existent");

        u.setUsername(req.username());
        u.setEmail(req.email());
        u.setPassword(req.password());
        u = repo.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    @Override @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public UserResponse get(Long id) {
        User u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User inexistent"));
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    @Override
    public List<UserResponse> list() {
        return repo.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail()))
                .toList();
    }
}

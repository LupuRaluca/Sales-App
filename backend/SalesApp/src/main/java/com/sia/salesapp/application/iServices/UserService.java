package com.sia.salesapp.application.iServices;

import com.sia.salesapp.domain.entity.User;
import com.sia.salesapp.web.dto.*;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse create(UserRequest req);

    UserResponse update(Long id, UserRequest req);

    void delete(Long id);

    UserResponse get(Long id);

    List<UserResponse> list();
}
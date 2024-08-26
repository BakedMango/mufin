package com.a502.backend.domain.user;

import com.a502.backend.application.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    @Autowired
    UserRepository userRepository;

    public User createAndSaveUser(String name) {
        User user = User.builder()
                .name(name)
                .email(name + "@email.com")
                .build();
        return userRepository.save(user);
    }

    public User createUser(String name) {
        return User.builder()
                .name(name)
                .email(name + "@email.com")
                .build();
    }
}

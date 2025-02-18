package com.sunlacey.service;

import com.sunlacey.eneity.User;
import com.sunlacey.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(String username, String displayName) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setActive(true);
        return userRepository.save(user);
    }

    public void deactivateUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
        }
    }
}
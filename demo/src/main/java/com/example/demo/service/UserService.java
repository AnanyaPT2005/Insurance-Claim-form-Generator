package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repository,
            BCryptPasswordEncoder encoder) {

        this.userRepository = repository;
        this.encoder = encoder;
    }

    public User saveUser(User user) {

        user.setPasswordHash(
                encoder.encode(user.getPasswordHash()));

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
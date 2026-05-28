package com.example.springecom.service;

import com.example.springecom.exception.UserNotFoundException;
import com.example.springecom.model.User;
import com.example.springecom.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo repo;

    @Autowired
    PasswordEncoder encoder;

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public User findByUserName(String userName) {
        return repo.findByUsername(userName).orElseThrow(new UserNotFoundException("cant find user"));
    }
}

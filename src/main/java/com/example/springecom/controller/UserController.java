package com.example.springecom.controller;

import com.example.springecom.model.User;
import com.example.springecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/register")
    public User login(@RequestBody User user) {
        return service.saveUser(user);
    }
}

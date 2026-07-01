package com.example.springecom.controller;

import com.example.springecom.model.User;
import com.example.springecom.model.dto.AuthRequest;
import com.example.springecom.model.dto.AuthResponse;
import com.example.springecom.model.dto.RegisterRequest;
import com.example.springecom.model.dto.UserResponse;
import com.example.springecom.service.JwtService;
import com.example.springecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User savedUser = userService.registerUser(request);
        String token = jwtService.generateToken(savedUser.getUsername());
        UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getName());
        return new ResponseEntity<>(new AuthResponse(token, userResponse), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println("Login attempt for email: " + request.email());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(request.email());
            User user = userService.findByUserName(request.email());

            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getName()
                    );

            return ResponseEntity.ok(new AuthResponse(token, userResponse));
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.findByUserName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                null);
        return ResponseEntity.ok(userResponse);
    }
}

package com.cst438.controllers;

import com.cst438.domain.User;
import com.cst438.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.dto.AccountCredentialsDTO;
import com.cst438.services.JwtService;
@RestController
public class LoginController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> getToken(@RequestBody AccountCredentialsDTO credentials) {
        UsernamePasswordAuthenticationToken creds =
                new UsernamePasswordAuthenticationToken(
                        credentials.username(),
                        credentials.password());

        Authentication auth = authenticationManager.authenticate(creds);

        // Generate token
        String jwts = jwtService.getToken(auth.getName());
        User user = userRepository.findByUsername(credentials.username());

        // Build response with the generated token and user role in the body
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(user.getRole()); // Assuming role is stored in authorities
    }
}

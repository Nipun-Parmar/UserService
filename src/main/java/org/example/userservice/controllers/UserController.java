package org.example.userservice.controllers;

import org.example.userservice.dtos.LoginRequestDto;
import org.example.userservice.dtos.LogoutRequestDto;
import org.example.userservice.dtos.SignUpRequestDto;
import org.example.userservice.dtos.UserDto;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto request){
        return userService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto request){
        String email = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();
        return UserDto.from(userService.signUp(name, email, password));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request){
        userService.logout(request.getToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable("token") @NonNull String token){
        return UserDto.from(userService.validateToken(token));
    }
}

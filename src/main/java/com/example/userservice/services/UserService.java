package com.example.userservice.services;

import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repos.TokenRepo;
import com.example.userservice.repos.UserRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private TokenRepo tokenRepo;
    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, TokenRepo tokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
    }

    public User signUp(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(password));

        return userRepo.save(user);
    }

    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }

        User user = optionalUser.get();
        if(!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new UsernameNotFoundException("User email and password do not match");
        }

        Token token = generateToken(user);

        return tokenRepo.save(token);
    }

    private Token generateToken(User user) {
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(10));
        token.setExpiryAt(System.currentTimeMillis() + 3600000);

        return token;
    }

    public User validateToken(String token) {
        /*
        A token is valid if:
        1. it exist in DB
        2. it has not expired
        3. it is not marked deleted
         */
        Optional<Token> tokenResult = tokenRepo
                .findByValueAndDeletedAndExpiryAtGreaterThan(token, false, System.currentTimeMillis());

        if(tokenResult.isEmpty()) {
            return null;
        }

        return tokenResult.get().getUser();
    }
}

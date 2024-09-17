package org.example.userservice.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.repositories.TokenRepository;
import org.example.userservice.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public User signUp(String fullName, String email, String password) {
        User u = new User();
        u.setEmail(email);
        u.setName(fullName);
        u.setHashedPassword(bCryptPasswordEncoder.encode(password));
        User user = userRepository.save(u);
        return user;
    }

    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            //User not exists exception
            return null;
        }
        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getHashedPassword())){
            //throw password not matching exception
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plus(30, ChronoUnit.DAYS);
        //Convert LocalDate to Date
        Date expiryDate = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Token token = new Token();
        token.setUser(user);
        token.setExpiryAt(expiryDate);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        Token savedToken = tokenRepository.save(token);

        return savedToken;
    }

    public void logout(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndIsDeleted(token, false);
        if(tokenOptional.isEmpty()){
            //Throw TokenNotExistOrAlreadyExpiredException();
            return;
        }
        Token token1 = tokenOptional.get();
        token1.setIsDeleted(true);
        tokenRepository.save(token1);
        return;
    }

    public User validateToken(String token) {
        Optional<Token> tokenOptional =
                tokenRepository.findByValueAndIsDeletedAndExpiryAtGreaterThan(token, false,new Date());
        if(tokenOptional.isEmpty()){
            return null;
        }
        return tokenOptional.get().getUser();
    }
}

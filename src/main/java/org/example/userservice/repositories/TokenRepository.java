package org.example.userservice.repositories;

import org.example.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);
    Optional<Token> findByValueAndIsDeleted(String value, boolean isDeleted);

    Optional<Token> findByValueAndIsDeletedAndExpiryAtGreaterThan(String value, boolean isDeleted, Date expiryGreaterThan);
}

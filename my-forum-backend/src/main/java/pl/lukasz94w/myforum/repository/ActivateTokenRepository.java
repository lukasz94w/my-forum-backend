package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.ActivateToken;

@Repository
public interface ActivateTokenRepository extends JpaRepository<ActivateToken, Long> {
    ActivateToken findByToken(String token);
}

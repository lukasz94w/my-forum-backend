package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lukasz94w.myforum.model.Ban;
import pl.lukasz94w.myforum.model.User;

public interface BanRepository extends JpaRepository<Ban, Long> {
    Ban findByUser(User user);
}

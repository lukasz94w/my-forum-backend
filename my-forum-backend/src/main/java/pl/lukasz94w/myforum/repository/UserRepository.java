package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u left join fetch u.roles where u.name = :username")
    Optional<User> findUserByName(@Param("username") String username);

    User findByName(String name);

    Boolean existsByName(String username);

    Boolean existsByEmail(String email);

    @Override
    @Query("select distinct u from User u join fetch u.roles")
    List<User> findAll();

    Optional<User> findByEmail(String email);


}

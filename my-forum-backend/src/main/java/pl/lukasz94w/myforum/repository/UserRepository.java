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

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.name = :username")
    Optional<User> findUserByName(@Param("username") String username);

    User findByName(String name);

    Boolean existsByName(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query(value =
            "SELECT MIN(post.user), COUNT(post.user) " +
                    "FROM Post post " +
                    "WHERE post.user.id IN :userIds " +
                    "GROUP BY post.user")
    List<Object[]> countPostsInPageableUsers(@Param("userIds") List<Long> userIds);

    @Query(value =
            "SELECT MIN(topic.user), COUNT(topic.user) " +
                    "FROM Topic topic " +
                    "WHERE topic.user.id IN :userIds " +
                    "GROUP BY topic.user")
    List<Object[]> countTopicsInPageableUsers(@Param("userIds") List<Long> userIds);
}

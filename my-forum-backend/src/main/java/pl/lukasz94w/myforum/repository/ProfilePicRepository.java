package pl.lukasz94w.myforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukasz94w.myforum.model.ProfilePic;

import java.util.Optional;

@Repository
public interface ProfilePicRepository extends JpaRepository<ProfilePic, Long> {
    Optional<ProfilePic> findById(long id);
}

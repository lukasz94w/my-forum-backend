package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.ProfilePicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;

@Service
public final class UserService {

    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProfilePicRepository profilePicRepository) {
        this.userRepository = userRepository;
        this.profilePicRepository = profilePicRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByName(username).orElseThrow(
                () -> new RuntimeException("Hello") //TODO implement my custom Exception
        );
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public ProfilePic saveProfilePicToDb(ProfilePic profilePic) {
        return profilePicRepository.save(profilePic);
    }

    public ProfilePic getProfilePic(Long id) {
        return profilePicRepository.findById(id).orElse(null);
    }
}

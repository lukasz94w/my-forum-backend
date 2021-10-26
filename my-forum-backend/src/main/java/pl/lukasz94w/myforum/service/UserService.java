package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.model.User;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new RuntimeException("Hello") //TODO implement my custom Exception
        );
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}

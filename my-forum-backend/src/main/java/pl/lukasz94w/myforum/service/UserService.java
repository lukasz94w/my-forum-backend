package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.ProfilePicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;

import java.util.HashMap;
import java.util.Map;

@Service
public final class UserService {

    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, ProfilePicRepository profilePicRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profilePicRepository = profilePicRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByName(username).orElseThrow(
                () -> new RuntimeException("Hello") //TODO implement my custom Exception
        );
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Map<String, byte[]> getProfilePic(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());
        ProfilePic profilePic = profilePicRepository.findById(authenticatedUser.getId()).orElse(null);

        Map<String, byte[]> mapWithProfilePic = new HashMap<>();
        if (profilePic != null) {
            mapWithProfilePic.put("rawData", profilePic.getData());
        } else {
            mapWithProfilePic.put("rawData", null);
        }

        return mapWithProfilePic;
    }

    public ResponseEntity<MessageResponse> updateProfilePic(MultipartFile image, Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());

        try {
            ProfilePic profilePic = profilePicRepository.save(new ProfilePic(authenticatedUser.getId(), image.getBytes()));
            authenticatedUser.setProfilePic(profilePic);
            userRepository.save(authenticatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Good"));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Bad!"));
        }
    }

    public ResponseEntity<?> changePassword(String currentPassword, String newPassword, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());

        if (passwordEncoder.matches(currentPassword, authenticatedUser.getPassword())) {
            authenticatedUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(authenticatedUser);
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        }
        else {
            return ResponseEntity.badRequest().body(new MessageResponse("Current password is not correct"));
        }
    }
}

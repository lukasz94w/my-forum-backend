package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;
import pl.lukasz94w.myforum.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @PostMapping("/updateProfilePic")
    public ResponseEntity<MessageResponse> updateProfilePic(@RequestParam("image") MultipartFile image, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userService.findUserByUsername(userDetailsImpl.getUsername());

        try {
            ProfilePic profilePic = userService.saveProfilePicToDb(new ProfilePic(authenticatedUser.getId(), image.getBytes()));
            authenticatedUser.setProfilePic(profilePic);
            userService.saveUser(authenticatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Good"));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Bad!"));
        }
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @GetMapping("/getProfilePic")
    public Map<String, byte[]> getProfilePic(Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userService.findUserByUsername(userDetailsImpl.getUsername());

        ProfilePic profilePic = userService.getProfilePic(authenticatedUser.getId());

        //raczej to powinienem jakos w userDto zwracac xD
        //i powinno to byc raczej getUserInfo jakos tak
        Map<String, byte[]> map = new HashMap<>();
        if (profilePic != null) {
            map.put("rawData", profilePic.getData());
        }
        else {
            map.put("rawData", null);
        }

        return map;
    }
}

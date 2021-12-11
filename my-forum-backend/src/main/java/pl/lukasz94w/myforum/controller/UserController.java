package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.request.ChangePasswordRequest;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.service.UserService;

import javax.validation.Valid;
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
    @PostMapping("/changeProfilePic")
    public ResponseEntity<MessageResponse> changeProfilePic(@RequestParam("image") MultipartFile image, Authentication authentication) {
        return userService.changeProfilePic(image, authentication);
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @GetMapping("/getProfilePic")
    public Map<String, byte[]> getProfilePic(Authentication authentication) {
        return userService.getProfilePic(authentication);
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        return userService.changePassword(request.getCurrentPassword(), request.getNewPassword(), authentication);
    }

}

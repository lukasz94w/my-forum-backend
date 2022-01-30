package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.request.BanRequest;
import pl.lukasz94w.myforum.request.ChangePasswordThroughUserSettings;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.response.UserDto;
import pl.lukasz94w.myforum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole ('USER')")
    @PostMapping("/changeProfilePic")
    public ResponseEntity<MessageResponse> changeProfilePic(@RequestParam("image") MultipartFile image, Authentication authentication) {
        return userService.changeProfilePic(image, authentication);
    }

    @PreAuthorize("hasRole ('USER')")
    @GetMapping("/getProfilePic")
    public Map<String, byte[]> getProfilePic(Authentication authentication) {
        return userService.getProfilePic(authentication);
    }

    @GetMapping("/getUserInfo/{username}")
    public UserDto getUserInfo(@PathVariable String username) {
        return userService.getUserInfo(username);
    }

    @PreAuthorize("hasRole ('USER')")
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordThroughUserSettings request, Authentication authentication) {
        return userService.changePasswordThroughUserSettings(request.getCurrentPassword(), request.getNewPassword(), authentication);
    }

    @GetMapping("/findPageablePostsByUser")
    public Map<String, Object> findPageablePostsByUser(@RequestParam(defaultValue = "0") int page, @RequestParam String username) {
        return userService.findPageablePostsByUser(page, username);
    }

    @GetMapping("/findPageableTopicsByUser")
    public Map<String, Object> findPageableTopicsByUser(@RequestParam(defaultValue = "0") int page, @RequestParam String username) {
        return userService.findPageableTopicsByUser(page, username);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @GetMapping("/findPageableUsers")
    public ResponseEntity<Map<String, Object>> findPageableUsers(@RequestParam(defaultValue = "0") int page) {
        return new ResponseEntity<>(userService.findPageableUsers(page), HttpStatus.OK);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PostMapping("/banUser")
    public ResponseEntity<HttpStatus> banUser(@Valid @RequestBody BanRequest banRequest) {
        return userService.banUser(banRequest);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PostMapping("/unBanUser")
    public ResponseEntity<HttpStatus> unBanUser(@RequestBody @NotBlank String userName) {
        return userService.unBanUser(userName);
    }
}

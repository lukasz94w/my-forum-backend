package pl.lukasz94w.myforum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.request.ChangePasswordViaUserSettings;
import pl.lukasz94w.myforum.response.dto.UserDto;
import pl.lukasz94w.myforum.response.message.SuccessResponse;
import pl.lukasz94w.myforum.service.UserService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/getUserInfo/{username}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
        return new ResponseEntity<>(userService.getUserInfo(username), HttpStatus.OK);
    }

    @PreAuthorize("hasRole ('USER')")
    @PutMapping("/changeProfilePic")
    public ResponseEntity<SuccessResponse> changeProfilePic(@RequestParam("image") MultipartFile image) {
        return new ResponseEntity<>(userService.changeProfilePic(image), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole ('USER')")
    @GetMapping("/getProfilePic")
    public ResponseEntity<Map<String, byte[]>> getProfilePic() {
        return new ResponseEntity<>(userService.getProfilePic(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole ('USER')")
    @PutMapping("/changePassword")
    public ResponseEntity<SuccessResponse> changePassword(@Valid @RequestBody ChangePasswordViaUserSettings request) {
        return new ResponseEntity<>(userService.changePasswordThroughUserSettings(request), HttpStatus.CREATED);
    }

    @GetMapping("/findPageablePostsByUser")
    public ResponseEntity<Map<String, Object>> findPageablePostsByUser(@RequestParam(defaultValue = "0") int page, @RequestParam String username) {
        return new ResponseEntity<>(userService.findPageablePostsByUser(page, username), HttpStatus.OK);
    }

    @GetMapping("/findPageableTopicsByUser")
    public ResponseEntity<Map<String, Object>> findPageableTopicsByUser(@RequestParam(defaultValue = "0") int page, @RequestParam String username) {
        return new ResponseEntity<>(userService.findPageableTopicsByUser(page, username), HttpStatus.OK);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @GetMapping("/findPageableUsers/{page}")
    public ResponseEntity<Map<String, Object>> findPageableUsers(@PathVariable int page) {
        return new ResponseEntity<>(userService.findPageableUsers(page), HttpStatus.OK);
    }
}

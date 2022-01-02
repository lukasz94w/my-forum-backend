package pl.lukasz94w.myforum.service;

import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.exception.UserNotFoundException;
import pl.lukasz94w.myforum.model.*;
import pl.lukasz94w.myforum.repository.*;
import pl.lukasz94w.myforum.request.ChangePasswordThroughEmail;
import pl.lukasz94w.myforum.request.SendResetEmailRequest;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.response.dto.PostDto2;
import pl.lukasz94w.myforum.response.dto.TopicDto2;
import pl.lukasz94w.myforum.response.dto.UserDto;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;
import pl.lukasz94w.myforum.service.util.MailUtil;
import pl.lukasz94w.myforum.service.util.TopicServiceUtil;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pl.lukasz94w.myforum.service.util.TopicServiceUtil.prepareLastActivitiesInPageableTopics;
import static pl.lukasz94w.myforum.service.util.TopicServiceUtil.prepareNumberOfAnswersInPageableTopics;

@Service
public final class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final TopicRepository topicRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final MailService mailService;

    @Autowired
    public UserService(UserRepository userRepository,
                       ProfilePicRepository profilePicRepository,
                       PostRepository postRepository,
                       PasswordEncoder passwordEncoder,
                       TopicRepository topicRepository,
                       PasswordTokenRepository passwordTokenRepository,
                       MailService mailService) {
        this.userRepository = userRepository;
        this.profilePicRepository = profilePicRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.topicRepository = topicRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.mailService = mailService;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByName(username).orElseThrow(
                () -> new UserNotFoundException("User with that name not found")
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

    public ResponseEntity<MessageResponse> changeProfilePic(MultipartFile image, Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());

        try {
            ProfilePic profilePic = profilePicRepository.save(new ProfilePic(authenticatedUser.getId(), image.getBytes()));
            authenticatedUser.setProfilePic(profilePic);
            userRepository.save(authenticatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Image changed"));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Error during changing"));
        }
    }

    public ResponseEntity<?> changePasswordThroughUserSettings(String currentPassword, String newPassword, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());

        if (passwordEncoder.matches(currentPassword, authenticatedUser.getPassword())) {
            authenticatedUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(authenticatedUser);
            return ResponseEntity
                    .ok(new MessageResponse("Password changed successfully"));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Current password is not correct"));
        }
    }

    public Map<String, Object> findPageablePostsByUser(int page, String username) {

        Pageable paging = PageRequest.of(page, 10, Sort.by("dateTime").descending());
        User user = userRepository.findByName(username);
        Page<Post> pageablePosts = postRepository.findByUser(user, paging);

        Collection<PostDto2> pageablePostsDto2 = pageablePosts.stream()
                .map(MapperDto::mapToPostDto2)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pageablePosts", pageablePostsDto2);
        response.put("currentPage", pageablePosts.getNumber());
        response.put("totalPosts", pageablePosts.getTotalElements());
        response.put("totalPages", pageablePosts.getTotalPages());

        return response;
    }

    public Map<String, Object> findPageableTopicsByUser(int page, String username) {
        User user = userRepository.findByName(username);
        Pageable paging = PageRequest.of(page, 10, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findByUser(user, paging);

        List<Topic> listOfLatest10Topics = pageableTopics.getContent();
        Collection<TopicDto2> pageableTopicsDto = listOfLatest10Topics.stream()
                .map(MapperDto::mapToTopicDto2)
                .collect(Collectors.toList());

        List<Long> listOfTopicIds = pageableTopics.stream().map(Topic::getId).collect(Collectors.toList());
        List<Object[]> foundedNumberOfPostsInPageableTopics = postRepository.countPostsInPageableTopics(listOfTopicIds);
        List<Long> numberOfAnswersInPageableTopics = prepareNumberOfAnswersInPageableTopics(listOfLatest10Topics, foundedNumberOfPostsInPageableTopics);

        List<Post> listOfLatestPosts = postRepository.findLatestPostsInEachOfLatestTopics(listOfTopicIds);
        List<TopicServiceUtil.LastActivityInPageableTopic> lastPageableTopicActivities = prepareLastActivitiesInPageableTopics(listOfLatest10Topics, listOfLatestPosts);

        Map<String, Object> response = new HashMap<>();
        response.put("pageableTopics", pageableTopicsDto);
        response.put("numberOfPostsInPageableTopics", numberOfAnswersInPageableTopics);
        response.put("lastPageableTopicActivities", lastPageableTopicActivities);
        response.put("currentPage", pageableTopics.getNumber());
        response.put("totalTopics", pageableTopics.getTotalElements());
        response.put("totalPages", pageableTopics.getTotalPages());

        return response;
    }

    public UserDto getUserInfo(String username) {
        return MapperDto.mapToUserDto(userRepository.findByName(username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with this email not found")
        );
    }

    public void sendEmailWithResetToken(SendResetEmailRequest sendResetEmailRequest) {
        String userEmail = sendResetEmailRequest.getEmail();
        try {
            User userFoundedByEmail = findByEmail(sendResetEmailRequest.getEmail());
            String token = RandomString.make(30);
            String resetPasswordLink = MailUtil.constructResetPasswordLink(token);
            this.passwordTokenRepository.save(new PasswordToken(userFoundedByEmail, token));
            this.mailService.sendMail(userEmail, resetPasswordLink);
        } catch (UserNotFoundException exception) {
            //application doesn't return result if the user with
            //such email exist, so app can f.e. log this event for information purposes
            logger.error(exception.getMessage());
        } catch (MessagingException | UnsupportedEncodingException exception) {
            logger.error(exception.getMessage());
        }
    }

    public ResponseEntity<?> changePasswordThroughEmail(ChangePasswordThroughEmail changePasswordThroughEmail) {

        PasswordToken passwordToken = passwordTokenRepository.findByToken(changePasswordThroughEmail.getReceivedToken());
        if (passwordToken == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Token not found"));
        }
        if (passwordToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(new MessageResponse("Token expired!"));
        }

        User user = userRepository.findByName(passwordToken.getUser().getName());
        user.setPassword(passwordEncoder.encode(changePasswordThroughEmail.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Password changed successfully"));
    }
}

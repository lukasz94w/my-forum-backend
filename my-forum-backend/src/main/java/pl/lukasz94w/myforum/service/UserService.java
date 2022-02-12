package pl.lukasz94w.myforum.service;

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
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.ProfilePicRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.response.MessageResponse;
import pl.lukasz94w.myforum.response.PostDto2;
import pl.lukasz94w.myforum.response.TopicDto2;
import pl.lukasz94w.myforum.response.UserDto;
import pl.lukasz94w.myforum.response.mapper.MapperDto;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;
import pl.lukasz94w.myforum.service.util.TopicServiceUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pl.lukasz94w.myforum.service.util.TopicServiceUtil.prepareLastActivitiesInPageableTopics;
import static pl.lukasz94w.myforum.service.util.TopicServiceUtil.prepareNumberOfAnswersInPageableTopics;
import static pl.lukasz94w.myforum.service.util.UserServiceUtil.prepareNumberOfEntriesInPageableUsers;

@Service
public final class UserService {

    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final TopicRepository topicRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       ProfilePicRepository profilePicRepository,
                       PostRepository postRepository,
                       PasswordEncoder passwordEncoder,
                       TopicRepository topicRepository) {
        this.userRepository = userRepository;
        this.profilePicRepository = profilePicRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.topicRepository = topicRepository;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Map<String, byte[]> getProfilePic(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());
        ProfilePic profilePic = authenticatedUser.getProfilePic();

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
        return MapperDto.mapToUserDto2(userRepository.findByName(username));
    }

    public Map<String, Object> findPageableUsers(int page) {
        Pageable paging = PageRequest.of(page, 10, Sort.by("registered").ascending());
        Page<User> pageableUsers = userRepository.findAll(paging);
        List<User> listOfLatest10PageableUsers = pageableUsers.getContent();

        Collection<UserDto> pageableUsersDto = listOfLatest10PageableUsers.stream()
                .map(MapperDto::mapToUserDto)
                .collect(Collectors.toList());

        List<Long> listOfUserIds = pageableUsers.stream().map(User::getId).collect(Collectors.toList());
        List<Object[]> foundedNumberOfPostsInPageableUsers = userRepository.countPostsInPageableUsers(listOfUserIds);
        List<Object[]> foundedNumberOfTopicsInPageableUsers = userRepository.countTopicsInPageableUsers(listOfUserIds);
        List<Long> numberOfPostsInPageableUsers = prepareNumberOfEntriesInPageableUsers(listOfLatest10PageableUsers, foundedNumberOfPostsInPageableUsers);
        List<Long> numberOfTopicsInPageableUsers = prepareNumberOfEntriesInPageableUsers(listOfLatest10PageableUsers, foundedNumberOfTopicsInPageableUsers);

        Map<String, Object> response = new HashMap<>();
        response.put("pageableUsers", pageableUsersDto);
        response.put("numberOfPostsInPageableUsers", numberOfPostsInPageableUsers);
        response.put("numberOfTopicsInPageableUsers", numberOfTopicsInPageableUsers);
        response.put("currentPage", pageableUsers.getNumber());
        response.put("totalUsers", pageableUsers.getTotalElements());
        response.put("totalPages", pageableUsers.getTotalPages());

        return response;
    }
}

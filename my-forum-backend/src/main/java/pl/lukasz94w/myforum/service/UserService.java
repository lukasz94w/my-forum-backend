package pl.lukasz94w.myforum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.exception.exception.ChangePasswordViaUserSettingsException;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
import pl.lukasz94w.myforum.exception.exception.ProfilePicUploadException;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.ProfilePicRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.ChangePasswordViaUserSettings;
import pl.lukasz94w.myforum.response.dto.LastActivityInPageableTopicDto;
import pl.lukasz94w.myforum.response.dto.PostDto2;
import pl.lukasz94w.myforum.response.dto.TopicDto2;
import pl.lukasz94w.myforum.response.dto.UserDto;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;
import pl.lukasz94w.myforum.response.message.SuccessResponse;
import pl.lukasz94w.myforum.security.auth.AuthorizedUserProvider;
import pl.lukasz94w.myforum.service.util.TopicServiceUtil;
import pl.lukasz94w.myforum.service.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final TopicRepository topicRepository;
    private final AuthorizedUserProvider authorizedUserProvider;
    private final TopicServiceUtil topicServiceUtil;
    private final UserServiceUtil userServiceUtil;
    private final MapperDto mapperDto;
    @Value("${pl.lukasz94w.pageableItemsNumber}")
    private int pageableUsersNumber;

    public UserDto getUserInfo(String userName) {
        return mapperDto.mapToUserDto2(checkIfUserExist(userName));
    }

    public Map<String, byte[]> getProfilePic() {
        User authenticatedUser = checkIfUserExist(authorizedUserProvider.getAuthorizedUserName());
        Optional<ProfilePic> profilePic = Optional.ofNullable(authenticatedUser.getProfilePic());
        return profilePic.map(pic -> Collections.singletonMap("rawData", pic.getData())).orElseGet(() -> Collections.singletonMap("rawData", null));
    }

    public SuccessResponse changeProfilePic(MultipartFile image) {
        User authenticatedUser = checkIfUserExist(authorizedUserProvider.getAuthorizedUserName());
        ProfilePic profilePic;

        try {
            profilePic = profilePicRepository.save(new ProfilePic(authenticatedUser.getId(), image.getBytes()));
        } catch (IOException exception) {
            throw new ProfilePicUploadException("Error during changing. Try again later");
        }

        authenticatedUser.setProfilePic(profilePic);
        userRepository.save(authenticatedUser);
        return new SuccessResponse("Image changed");
    }

    public SuccessResponse changePasswordThroughUserSettings(ChangePasswordViaUserSettings request) {
        User authenticatedUser = checkIfUserExist(authorizedUserProvider.getAuthorizedUserName());

        if (passwordEncoder.matches(request.getCurrentPassword(), authenticatedUser.getPassword())) {
            authenticatedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(authenticatedUser);
            return new SuccessResponse("Password changed successfully");
        } else {
            throw new ChangePasswordViaUserSettingsException("Current password is not correct");
        }
    }

    public Map<String, Object> findPageablePostsByUser(int page, String username) {
        Pageable paging = PageRequest.of(page, pageableUsersNumber, Sort.by("dateTime").descending());
        User user = userRepository.findByName(username);
        Page<Post> pageablePosts = postRepository.findByUser(user, paging);

        Collection<PostDto2> pageablePostsDto2 = pageablePosts.stream()
                .map(mapperDto::mapToPostDto2)
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
        Pageable paging = PageRequest.of(page, pageableUsersNumber, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findByUser(user, paging);

        List<Topic> listOfPageableTopics = pageableTopics.getContent();
        Collection<TopicDto2> pageableTopicsDto = listOfPageableTopics.stream()
                .map(mapperDto::mapToTopicDto2)
                .collect(Collectors.toList());

        List<Long> listOfTopicIds = pageableTopics.stream().map(Topic::getId).collect(Collectors.toList());
        List<Object[]> foundedNumberOfPostsInPageableTopics = postRepository.countPostsInPageableTopics(listOfTopicIds);
        List<Long> numberOfAnswersInPageableTopics = topicServiceUtil.prepareNumberOfAnswersInPageableTopics(listOfPageableTopics, foundedNumberOfPostsInPageableTopics);

        List<Post> listOfLatestPosts = postRepository.findLatestPostsInEachOfLatestTopics(listOfTopicIds);
        List<LastActivityInPageableTopicDto> lastPageableTopicActivities = topicServiceUtil.prepareLastActivitiesInPageableTopics(listOfPageableTopics, listOfLatestPosts);

        Map<String, Object> response = new HashMap<>();
        response.put("pageableTopics", pageableTopicsDto);
        response.put("numberOfPostsInPageableTopics", numberOfAnswersInPageableTopics);
        response.put("lastPageableTopicActivities", lastPageableTopicActivities);
        response.put("currentPage", pageableTopics.getNumber());
        response.put("totalTopics", pageableTopics.getTotalElements());
        response.put("totalPages", pageableTopics.getTotalPages());

        return response;
    }

    public Map<String, Object> findPageableUsers(int page) {
        Pageable paging = PageRequest.of(page, pageableUsersNumber, Sort.by("registered").ascending());
        Page<User> pageableUsers = userRepository.findAll(paging);
        List<User> listOfPageableUsers = pageableUsers.getContent();

        Collection<UserDto> pageableUsersDto = listOfPageableUsers.stream()
                .map(mapperDto::mapToUserDto)
                .collect(Collectors.toList());

        List<Long> listOfUserIds = pageableUsers.stream().map(User::getId).collect(Collectors.toList());
        List<Object[]> foundedNumberOfPostsInPageableUsers = userRepository.countPostsInPageableUsers(listOfUserIds);
        List<Object[]> foundedNumberOfTopicsInPageableUsers = userRepository.countTopicsInPageableUsers(listOfUserIds);
        List<Long> numberOfPostsInPageableUsers = userServiceUtil.prepareNumberOfEntriesInPageableUsers(listOfPageableUsers, foundedNumberOfPostsInPageableUsers);
        List<Long> numberOfTopicsInPageableUsers = userServiceUtil.prepareNumberOfEntriesInPageableUsers(listOfPageableUsers, foundedNumberOfTopicsInPageableUsers);

        Map<String, Object> response = new HashMap<>();
        response.put("pageableUsers", pageableUsersDto);
        response.put("numberOfPostsInPageableUsers", numberOfPostsInPageableUsers);
        response.put("numberOfTopicsInPageableUsers", numberOfTopicsInPageableUsers);
        response.put("currentPage", pageableUsers.getNumber());
        response.put("totalUsers", pageableUsers.getTotalElements());
        response.put("totalPages", pageableUsers.getTotalPages());

        return response;
    }

    private User checkIfUserExist(String userName) {
        return userRepository.findUserByName(userName)
                .orElseThrow(() -> new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST));
    }
}

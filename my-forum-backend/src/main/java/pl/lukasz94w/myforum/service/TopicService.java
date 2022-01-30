package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.NewTopicContent;
import pl.lukasz94w.myforum.response.TopicDto;
import pl.lukasz94w.myforum.response.mapper.MapperDto;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;

import java.util.*;
import java.util.stream.Collectors;

import static pl.lukasz94w.myforum.service.util.TopicServiceUtil.*;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository, CategoryRepository categoryRepository, UserRepository userRepository, PostRepository postRepository) {
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<HttpStatus> createTopic(NewTopicContent newTopicContent, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());

        EnumeratedCategory enumeratedCategory = EnumeratedCategory.valueOf(newTopicContent.getCategory().toUpperCase());
        Category topicCategory = categoryRepository.findByEnumeratedCategory(enumeratedCategory);

        Topic newTopic = new Topic(newTopicContent.getTitle(), authenticatedUser, topicCategory);
        this.topicRepository.save(newTopic);
        Post firstPostUnderNewTopic = new Post(newTopicContent.getContent(), 1, newTopic, authenticatedUser);
        this.postRepository.save(firstPostUnderNewTopic);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public ResponseEntity<HttpStatus> deleteTopicById(final Long id) {
        topicRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public TopicDto getTopicById(final Long id) {
        return MapperDto.mapToTopicDto(topicRepository.getById(id));
    }

    public List<TopicDto> getAllTopics() {
        Collection<Topic> topics = topicRepository.findAll();

        return topics.stream()
                .map(MapperDto::mapToTopicDto)
                .collect(Collectors.toList());
    }

    public Map<String, Object> findPageableTopicsInCategory(int page, String category) {
        Category chosenCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.valueOf(category.toUpperCase(Locale.ROOT)));
        Pageable paging = PageRequest.of(page, 10, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findTopicsByCategory(chosenCategory, paging);
        return buildResponse(pageableTopics);
    }

    public Map<String, Object> countTopicsAndPostsByCategory() {
        List<Object[]> countedTopicsByCategories = topicRepository.countTopicsByCategories();
        List<Long> numberOfTopicsInEachCategory = prepareNumbersOfEntries(countedTopicsByCategories);

        List<Object[]> countedPostsByCategories = postRepository.countPostsByCategories();
        List<Long> numberOfPostsInEachCategory = prepareNumbersOfEntries(countedPostsByCategories);

        long totalNumberOfEntriesInGeneralSubjects =
                numberOfTopicsInEachCategory.get(0) +
                        numberOfTopicsInEachCategory.get(1) +
                        numberOfTopicsInEachCategory.get(2) +
                        numberOfTopicsInEachCategory.get(3) +
                        numberOfPostsInEachCategory.get(0) +
                        numberOfPostsInEachCategory.get(1) +
                        numberOfPostsInEachCategory.get(2) +
                        numberOfPostsInEachCategory.get(3);

        long totalSumOfEntriesInOtherSubjects =
                numberOfTopicsInEachCategory.get(4) +
                        numberOfTopicsInEachCategory.get(5) +
                        numberOfTopicsInEachCategory.get(6) +
                        numberOfPostsInEachCategory.get(4) +
                        numberOfPostsInEachCategory.get(5) +
                        numberOfPostsInEachCategory.get(6);

        List<Topic> latestTopicsInEachCategory = topicRepository.findLatestTopicInEachCategory();
        List<Long> listOfTopicIds = latestTopicsInEachCategory.stream().map(Topic::getId).collect(Collectors.toList());
        List<Post> latestPostInLatestTopics = postRepository.findLatestPostsInEachOfLatestTopics(listOfTopicIds);
        List<LastActivityInCategory> lastTopicActivities = prepareLastActivitiesInEachCategory(latestTopicsInEachCategory, latestPostInLatestTopics);

        Map<String, Object> response = new HashMap<>();
        response.put("numberOfTopicsInEachCategory", numberOfTopicsInEachCategory);
        response.put("numberOfPostsInEachCategory", numberOfPostsInEachCategory);
        response.put("numberOfEntriesInGeneralSubjects", totalNumberOfEntriesInGeneralSubjects);
        response.put("numberOfEntriesInOtherSubjects", totalSumOfEntriesInOtherSubjects);
        response.put("lastTopicActivities", lastTopicActivities);

        return response;
    }

    public Map<String, Object> searchInTopicTitles(int page, String query) {
        Pageable paging = PageRequest.of(page, 10, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findByTitleContainsIgnoreCase(query, paging);
        return buildResponse(pageableTopics);
    }

    private Map<String, Object> buildResponse(Page<Topic> pageableTopics) {
        List<Topic> listOfLatest10Topics = pageableTopics.getContent();
        Collection<TopicDto> pageableTopicsDto = listOfLatest10Topics.stream()
                .map(MapperDto::mapToTopicDto2)
                .collect(Collectors.toList());

        List<Long> listOfTopicIds = pageableTopics.stream().map(Topic::getId).collect(Collectors.toList());
        List<Object[]> foundedNumberOfPostsInPageableTopics = postRepository.countPostsInPageableTopics(listOfTopicIds);
        List<Long> numberOfAnswersInPageableTopics = prepareNumberOfAnswersInPageableTopics(listOfLatest10Topics, foundedNumberOfPostsInPageableTopics);

        List<Post> listOfLatestPosts = postRepository.findLatestPostsInEachOfLatestTopics(listOfTopicIds);
        List<LastActivityInPageableTopic> lastPageableTopicActivities = prepareLastActivitiesInPageableTopics(listOfLatest10Topics, listOfLatestPosts);

        Map<String, Object> response = new HashMap<>();
        response.put("pageableTopics", pageableTopicsDto);
        response.put("numberOfPostsInPageableTopics", numberOfAnswersInPageableTopics);
        response.put("lastPageableTopicActivities", lastPageableTopicActivities);
        response.put("currentPage", pageableTopics.getNumber());
        response.put("totalTopics", pageableTopics.getTotalElements());
        response.put("totalPages", pageableTopics.getTotalPages());

        return response;
    }
}

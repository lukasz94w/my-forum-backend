package pl.lukasz94w.myforum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
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
import pl.lukasz94w.myforum.request.TopicStatus;
import pl.lukasz94w.myforum.response.dto.LastActivityInCategoryDto;
import pl.lukasz94w.myforum.response.dto.LastActivityInPageableTopicDto;
import pl.lukasz94w.myforum.response.dto.TopicDto;
import pl.lukasz94w.myforum.response.dto.TopicDto3;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;
import pl.lukasz94w.myforum.security.auth.AuthorizedUserProvider;
import pl.lukasz94w.myforum.service.util.TopicServiceUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TopicServiceUtil topicServiceUtil;
    private final MapperDto mapperDto;
    private final AuthorizedUserProvider authorizedUserProvider;

    public void createTopic(NewTopicContent newTopicContent) {
        User authenticatedUser = userRepository.findByName(authorizedUserProvider.getAuthorizedUserName());

        Category topicCategory = getCategoryIfExistOrThrowException(newTopicContent.getCategory());

        Topic newTopic = new Topic(newTopicContent.getTitle(), authenticatedUser, topicCategory);
        this.topicRepository.save(newTopic);
        Post firstPostUnderNewTopic = new Post(newTopicContent.getContent(), 1, newTopic, authenticatedUser);
        this.postRepository.save(firstPostUnderNewTopic);
    }

    public void changeStatus(TopicStatus topicStatus) {
        Topic topic = topicRepository.findTopicById(topicStatus.getTopicId());
        topic.setClosed(topicStatus.isClosed());
        topicRepository.save(topic);
    }

    public void deleteTopicById(Long id) {
        topicRepository.deleteById(id);
    }

    public TopicDto3 getTopicById(Long id) {
        return mapperDto.mapToTopicDto3(topicRepository
                .findById(id)
                .orElseThrow(() -> new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.TOPIC_DOESNT_EXIST)));
    }

    public Map<String, Object> findPageableTopicsInCategory(int page, String category) {
        Category chosenCategory = getCategoryIfExistOrThrowException(category);

        Pageable paging = PageRequest.of(page, 10, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findTopicsByCategory(chosenCategory, paging);
        return buildResponse(pageableTopics);
    }

    public Map<String, Object> countTopicsAndPostsByCategory() {
        List<Object[]> countedTopicsByCategories = topicRepository.countTopicsByCategories();
        List<Long> numberOfTopicsInEachCategory = topicServiceUtil.prepareNumbersOfEntries(countedTopicsByCategories);

        List<Object[]> countedPostsByCategories = postRepository.countPostsByCategories();
        List<Long> numberOfPostsInEachCategory = topicServiceUtil.prepareNumbersOfEntries(countedPostsByCategories);

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
        List<LastActivityInCategoryDto> lastTopicActivities = topicServiceUtil.prepareLastActivitiesInEachCategory(latestTopicsInEachCategory, latestPostInLatestTopics);

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
                .map(mapperDto::mapToTopicDto2)
                .collect(Collectors.toList());

        List<Long> listOfTopicIds = pageableTopics.stream().map(Topic::getId).collect(Collectors.toList());
        List<Object[]> foundedNumberOfPostsInPageableTopics = postRepository.countPostsInPageableTopics(listOfTopicIds);
        List<Long> numberOfAnswersInPageableTopics = topicServiceUtil.prepareNumberOfAnswersInPageableTopics(listOfLatest10Topics, foundedNumberOfPostsInPageableTopics);

        List<Post> listOfLatestPosts = postRepository.findLatestPostsInEachOfLatestTopics(listOfTopicIds);
        List<LastActivityInPageableTopicDto> lastPageableTopicActivities = topicServiceUtil.prepareLastActivitiesInPageableTopics(listOfLatest10Topics, listOfLatestPosts);

        Map<String, Object> response = new HashMap<>();
        response.put("pageableTopics", pageableTopicsDto);
        response.put("numberOfPostsInPageableTopics", numberOfAnswersInPageableTopics);
        response.put("lastPageableTopicActivities", lastPageableTopicActivities);
        response.put("currentPage", pageableTopics.getNumber());
        response.put("totalTopics", pageableTopics.getTotalElements());
        response.put("totalPages", pageableTopics.getTotalPages());

        return response;
    }

    private Category getCategoryIfExistOrThrowException(String category) {
        EnumeratedCategory enumeratedCategory = Arrays.stream(EnumeratedCategory.values())
                .filter(e -> e.name().equals(category.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.CATEGORY_DOESNT_EXIST));

        return categoryRepository.findByEnumeratedCategory(enumeratedCategory);
    }
}

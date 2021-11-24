package pl.lukasz94w.myforum.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.dtoConverter.DtoConverter;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicService {

    TopicRepository topicRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    PostRepository postRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository, CategoryRepository categoryRepository, UserRepository userRepository, PostRepository postRepository) {
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public TopicDto createTopic(Topic topic) {
        topicRepository.save(topic);
        return DtoConverter.convertTopicToTopicDto(topic);
    }

    public void deleteTopicById(final Long id) {
        topicRepository.deleteById(id);
    }

    public Topic findTopicById(final Long id) {
        return topicRepository.findTopicById(id);
    }

    public TopicDto getTopicById(final Long id) {
        return DtoConverter.convertTopicToTopicDto(topicRepository.getById(id));
    }

    public List<TopicDto> getAllTopics() {
        Collection<Topic> topics = topicRepository.findAll();

        return topics.stream()
                .map(DtoConverter::convertTopicToTopicDto)
                .collect(Collectors.toList());
    }

    public Integer countTopicByCategory(Category category) {
        return topicRepository.countTopicByCategory(category);
    }

    public List<Object[]> countByCategoryList() {
        return topicRepository.countByCategoryList();
    }

    public List<Topic> findLatestTopicInEachCategory() {
        return topicRepository.findLatestTopicInEachCategory();
    }

    public Map<String, Object> findLatestTopicsByCategory(int page, String category) {

        Category chosenCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.valueOf(category.toUpperCase(Locale.ROOT)));
        Pageable paging = PageRequest.of(page, 10, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findTopicsByCategory(chosenCategory, paging);
        List<Topic> listOfLatest10Topics = pageableTopics.getContent();
        Collection<TopicDto> topicsDto = listOfLatest10Topics.stream()
                .map(DtoConverter::convertTopicToTopicDto)
                .collect(Collectors.toList());

        List<Long> listOfTopicIds = pageableTopics.stream().map(Topic::getId).collect(Collectors.toList());
        List<Post> listOfLatestPosts = postRepository.findLatestPostsInPageableTopics(listOfTopicIds, chosenCategory);

        List<LastTopicActivity> lastTopicActivities = new ArrayList<>();
        for (Topic topic : listOfLatest10Topics) {
            Post latestPost = findLatestPostInTopic(topic, listOfLatestPosts);

            if (latestPost == null) {
                lastTopicActivities.add(new LastTopicActivity(topic.getTitle(), topic.getId(), topic.getUser().getUsername(), getProfilePicData(topic.getUser().getProfilePic()), topic.getDateTimeOfTopic()));
            } else {
                lastTopicActivities.add(new LastTopicActivity(topic.getTitle(), topic.getId(), latestPost.getUser().getUsername(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTimeOfPost()));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("topics", topicsDto);
        response.put("lastTopicActivities", lastTopicActivities);
        response.put("currentPage", pageableTopics.getNumber());
        response.put("totalTopics", pageableTopics.getTotalElements());
        response.put("totalPages", pageableTopics.getTotalPages());

        return response;
    }

    public Map<String, Object> countTopicsAndPostsByCategory() {
        List<Object[]> foundTopicsByCategoriesCount = topicRepository.countByCategoryList();
        List<Long> numberOfTopicsInEachCategory = prepareFullListOfNumberOfEntries(foundTopicsByCategoriesCount);

        List<Object[]> postByCategoriesCount = postRepository.countByCategoryList();
        List<Long> numberOfPostsInEachCategory = prepareFullListOfNumberOfEntries(postByCategoriesCount);

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
        List<LastTopicActivity> lastTopicActivities = prepareListOfLastActivitiesInEachCategory(latestTopicsInEachCategory, latestPostInLatestTopics);

        Map<String, Object> response = new HashMap<>();
        response.put("numberOfTopicsInEachCategory", numberOfTopicsInEachCategory);
        response.put("numberOfPostsInEachCategory", numberOfPostsInEachCategory);
        response.put("numberOfEntriesInGeneralSubjects", totalNumberOfEntriesInGeneralSubjects);
        response.put("numberOfEntriesInOtherSubjects", totalSumOfEntriesInOtherSubjects);
        response.put("lastTopicActivities", lastTopicActivities);

        return response;
    }

    private List<Long> prepareFullListOfNumberOfEntries(List<Object[]> topicByCategoriesCount) {
        List<Long> fullListOfEntriesByCategoriesCount = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfEntriesByCategoriesCount.add(getCountOfEntriesInCategory(enumeratedCategory.toString(), topicByCategoriesCount));
        }

        return fullListOfEntriesByCategoriesCount;
    }

    private long getCountOfEntriesInCategory(String category, List<Object[]> topicByCategoriesCount) {

        for (Object[] object : topicByCategoriesCount) {
            if (object[1].toString().equals(category)) {
                return (long) object[0];
            }
        }
        return 0;
    }

    private Post findLatestPostInTopic(Topic topic, List<Post> listOfPosts) {

        for (Post post : listOfPosts) {
            if (post.getTopic().equals(topic))
                return post;
        }
        return null;
    }

    private List<LastTopicActivity> prepareListOfLastActivitiesInEachCategory(List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {
        List<LastTopicActivity> fullListOfLastActivitiesInCategory = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfLastActivitiesInCategory.add(findLastTopicInCategory(enumeratedCategory.toString(), latestTopicsInEachCategory, latestPostInEachOfLatestTopics));
        }

        return fullListOfLastActivitiesInCategory;
    }

    private LastTopicActivity findLastTopicInCategory(String category, List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {

        for (Topic latestTopicInEachCategory : latestTopicsInEachCategory) {
            if (latestTopicInEachCategory.getCategory().toString().equals(category)) {
                return getLatestActivityInTopic(latestTopicInEachCategory, latestPostInEachOfLatestTopics);
            }
        }
        return null;
    }

    private LastTopicActivity getLatestActivityInTopic(Topic foundTopic, List<Post> latestPostInEachOfLatestTopics) {
        LastTopicActivity lastTopicActivity;

        Post latestPost = findLatestPostInTopic(foundTopic, latestPostInEachOfLatestTopics);
        if (latestPost == null) {
            lastTopicActivity = new LastTopicActivity(foundTopic.getTitle(), foundTopic.getId(), foundTopic.getUser().getUsername(), getProfilePicData(foundTopic.getUser().getProfilePic()), foundTopic.getDateTimeOfTopic());
        } else {
            lastTopicActivity = new LastTopicActivity(foundTopic.getTitle(), foundTopic.getId(), latestPost.getUser().getUsername(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTimeOfPost());
        }

        return lastTopicActivity;
    }

    private byte[] getProfilePicData(ProfilePic profilePic) {
        if (profilePic != null) {
            return profilePic.getData();
        }
        return null;
    }

    @AllArgsConstructor
    @Getter
    private static class LastTopicActivity {
        private String topicName;
        private Long topicId;
        private String userName;
        private byte[] userProfilePic;
        private LocalDateTime timeOfLastActivity;
    }
}

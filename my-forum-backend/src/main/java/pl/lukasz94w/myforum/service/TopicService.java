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

        //get topics
        Category chosenCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.valueOf(category.toUpperCase(Locale.ROOT)));
        Pageable paging = PageRequest.of(page, 10, Sort.by("timeOfActualization").descending());
        Page<Topic> pageableTopics = topicRepository.findTopicsByCategory(chosenCategory, paging);
        List<Topic> listOfLatest10Topics = pageableTopics.getContent();

        //get latest posts of the topics
        List<Long> listOfTopicIds = pageableTopics.stream().map(Topic::getId).collect(Collectors.toList());
        List<Post> listOfLatestPosts = postRepository.findLatestPostsInPageableTopics(listOfTopicIds, chosenCategory);

        List<LastTopicActivity> lastTopicActivities = new ArrayList<>();
        for (Topic topic : listOfLatest10Topics) {

            Post latestPost = findLatestPostInTopic(topic, listOfLatestPosts);

            if (latestPost == null) {
                lastTopicActivities.add(new LastTopicActivity(topic.getUser().getUsername(), topic.getDateTimeOfTopic()));
            } else {
                lastTopicActivities.add(new LastTopicActivity(latestPost.getUser().getUsername(), latestPost.getDateTimeOfPost()));
            }
        }

        Collection<TopicDto> topicsDto = listOfLatest10Topics.stream()
                .map(DtoConverter::convertTopicToTopicDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("topics", topicsDto);
        response.put("lastTopicActivities", lastTopicActivities);
        response.put("currentPage", pageableTopics.getNumber());
        response.put("totalTopics", pageableTopics.getTotalElements());
        response.put("totalPages", pageableTopics.getTotalPages());

        return response;
    }

    @AllArgsConstructor
    @Getter
    private static class LastTopicActivity {
        private String user;
        private LocalDateTime timeOfLastActivity;
    }

    private Post findLatestPostInTopic(Topic topic, List<Post> listOfPosts) {

        for (Post post : listOfPosts) {
            if (post.getTopic().equals(topic))
                return post;
        }
        return null;
    }
}

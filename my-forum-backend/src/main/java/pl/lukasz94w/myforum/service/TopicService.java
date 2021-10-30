package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.dtoConverter.DtoConverter;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;

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

    public Map<String, Object> findAllTopicsByCategory(int page, String category) {

        Category chosenCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.valueOf(category.toUpperCase(Locale.ROOT)));
        Pageable paging = PageRequest.of(page, 10, Sort.by("dateTimeOfTopic").descending());

        Page<Topic> pageTopics = topicRepository.findAllTopicsByCategory(chosenCategory, paging);
        Collection<Topic> topics = pageTopics.getContent();
        Collection<TopicDto> topicsDto = topics.stream()
                .map(DtoConverter::convertTopicToTopicDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("topics", topicsDto);
        response.put("currentPage", pageTopics.getNumber());
        response.put("totalTopics", pageTopics.getTotalElements());
        response.put("totalPages", pageTopics.getTotalPages());

        return response;
    }
}

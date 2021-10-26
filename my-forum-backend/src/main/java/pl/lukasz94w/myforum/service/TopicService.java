package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.dtoConverter.DtoConverter;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TopicService {

    TopicRepository topicRepository;
    CategoryRepository categoryRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository, CategoryRepository categoryRepository) {
        this.topicRepository = topicRepository;
        this.categoryRepository = categoryRepository;
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

    public List<TopicDto> findAllTopicsByCategory(String categoryName) {

        Category chosenCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.valueOf(categoryName.toUpperCase(Locale.ROOT)));

        Collection<Topic> topics = topicRepository.findAllTopicsByCategory(chosenCategory);

        return topics.stream()
                .map(DtoConverter::convertTopicToTopicDto)
                .collect(Collectors.toList());
    }
}

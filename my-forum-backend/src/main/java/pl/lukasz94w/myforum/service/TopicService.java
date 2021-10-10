package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.dtoConverter.DtoConverter;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.repository.TopicRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicDto createTopic(Topic topic) {
        topicRepository.save(topic);
        return DtoConverter.convertTopicToTopicDto(topic);
    }

    public void deleteTopicById(final Long id) {
        topicRepository.deleteById(id);
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
}

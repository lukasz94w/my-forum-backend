package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.service.TopicService;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping("/addTopic")
    public ResponseEntity<TopicDto> createTopic(@RequestBody Topic topic) {
        return new ResponseEntity<>(this.topicService.createTopic(topic), HttpStatus.CREATED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity deletePostById(@PathVariable final Long id) {
        topicService.deleteTopicById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("getTopicById/{id}")
    public ResponseEntity<TopicDto> getTopicById(@PathVariable final Long id) {
        return new ResponseEntity<>(topicService.getTopicById(id), HttpStatus.OK);
    }

    @GetMapping("/getTopics")
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        return new ResponseEntity<>(this.topicService.getAllTopics(), HttpStatus.OK);
    }
}

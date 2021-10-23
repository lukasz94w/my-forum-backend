package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.postRequest.NewTopicContent;
import pl.lukasz94w.myforum.security.userDetails.UserDetailsImpl;
import pl.lukasz94w.myforum.service.TopicService;
import pl.lukasz94w.myforum.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;
    private final UserService userService;

    @Autowired
    public TopicController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @PostMapping("/addTopic")
    public ResponseEntity<TopicDto> createTopic(@RequestBody NewTopicContent newTopicContent, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userService.findUserByUsername(userDetailsImpl.getUsername());

        Topic newTopic = new Topic(newTopicContent.getTitle(), newTopicContent.getContent(), authenticatedUser);

        //TODO tutaj raczej nie zwracac calego posta tylko sam status CREATED...
        return new ResponseEntity<>(this.topicService.createTopic(newTopic), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole ('ADMIN')")
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
        //TODO tutaj mozna topic content wlasnie zwracac
        return new ResponseEntity<>(this.topicService.getAllTopics(), HttpStatus.OK);
    }
}

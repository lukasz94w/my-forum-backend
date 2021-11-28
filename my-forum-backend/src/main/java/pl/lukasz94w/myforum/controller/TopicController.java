package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.NewTopicContent;
import pl.lukasz94w.myforum.response.dto.TopicDto;
import pl.lukasz94w.myforum.service.TopicService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @PostMapping("/addTopic")
    public ResponseEntity createTopic(@RequestBody NewTopicContent newTopicContent, Authentication authentication) {
        topicService.createTopic(newTopicContent, authentication);
        return new ResponseEntity<>(HttpStatus.CREATED);
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

    @GetMapping("/findPageableTopicsInCategory")
    public ResponseEntity<Map<String, Object>> findPageableTopicsInCategory(@RequestParam(defaultValue = "0") int page, @RequestParam String category) {
        return new ResponseEntity<>(this.topicService.findPageableTopicsInCategory(page, category), HttpStatus.OK);
    }

    @GetMapping("/countTopicsAndPostsByCategory")
    public ResponseEntity<Map<String, Object>> countTopicsAndPostsByCategory() {
        return new ResponseEntity<>(this.topicService.countTopicsAndPostsByCategory(), HttpStatus.OK);
    }
}

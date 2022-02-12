package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.NewTopicContent;
import pl.lukasz94w.myforum.request.TopicStatus;
import pl.lukasz94w.myforum.response.TopicDto3;
import pl.lukasz94w.myforum.service.TopicService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PreAuthorize("hasRole ('USER')")
    @PostMapping("/addTopic")
    public ResponseEntity<HttpStatus> createTopic(@Valid @RequestBody NewTopicContent newTopicContent, Authentication authentication) {
        return topicService.createTopic(newTopicContent, authentication);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PostMapping("/changeStatus")
    public ResponseEntity<HttpStatus> changeStatus(@Valid @RequestBody TopicStatus topicStatus) {
        return topicService.changeStatus(topicStatus);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @GetMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteTopicById(@PathVariable final Long id) {
        return topicService.deleteTopicById(id);
    }

    @GetMapping("getTopicById/{id}")
    public ResponseEntity<TopicDto3> getTopicById(@PathVariable final Long id) {
        return new ResponseEntity<>(topicService.getTopicById(id), HttpStatus.OK);
    }

    @GetMapping("/findPageableTopicsInCategory")
    public ResponseEntity<Map<String, Object>> findPageableTopicsInCategory(@RequestParam(defaultValue = "0") int page, @RequestParam String category) {
        return new ResponseEntity<>(topicService.findPageableTopicsInCategory(page, category), HttpStatus.OK);
    }

    @GetMapping("/countTopicsAndPostsByCategory")
    public ResponseEntity<Map<String, Object>> countTopicsAndPostsByCategory() {
        return new ResponseEntity<>(topicService.countTopicsAndPostsByCategory(), HttpStatus.OK);
    }

    @GetMapping("/searchInTopicTitles")
    public ResponseEntity<Map<String, Object>> searchInTopicTitles(@RequestParam(defaultValue = "0") int page, @RequestParam String query) {
        return new ResponseEntity<>(topicService.searchInTopicTitles(page, query), HttpStatus.OK);
    }
}

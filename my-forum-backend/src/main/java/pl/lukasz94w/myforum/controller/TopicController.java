package pl.lukasz94w.myforum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.NewTopicContent;
import pl.lukasz94w.myforum.request.TopicStatus;
import pl.lukasz94w.myforum.response.dto.TopicDto3;
import pl.lukasz94w.myforum.service.TopicService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;

    @PreAuthorize("hasRole ('USER')")
    @PostMapping("/addTopic")
    public ResponseEntity<Long> createTopic(@Valid @RequestBody NewTopicContent newTopicContent) {
        return new ResponseEntity<>(topicService.createTopic(newTopicContent), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PutMapping("/changeStatus")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody TopicStatus topicStatus) {
        topicService.changeStatus(topicStatus);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTopicById(@PathVariable Long id) {
        topicService.deleteTopicById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("getTopicById/{id}")
    public ResponseEntity<TopicDto3> getTopicById(@PathVariable Long id) {
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

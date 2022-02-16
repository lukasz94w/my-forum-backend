package pl.lukasz94w.myforum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.NewPostContent;
import pl.lukasz94w.myforum.request.PostStatus;
import pl.lukasz94w.myforum.service.PostService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasRole ('USER')")
    @PostMapping("/addPost")
    public ResponseEntity<Void> addPost(@Valid @RequestBody NewPostContent newPostContent) {
        postService.addPost(newPostContent);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PutMapping("/changeStatus")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody PostStatus postStatus) {
        postService.changeStatus(postStatus);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/findPageablePostsByTopicId")
    public ResponseEntity<Map<String, Object>> findPageablePostsByTopicId(@RequestParam(defaultValue = "0") int page, @RequestParam Long id) {
        return new ResponseEntity<>(postService.findPageablePostsByTopicId(page, id), HttpStatus.OK);
    }

    @GetMapping("/searchInPosts")
    public ResponseEntity<Map<String, Object>> searchInPosts(@RequestParam(defaultValue = "0") int page, @RequestParam String query) {
        return new ResponseEntity<>(postService.searchInPosts(page, query), HttpStatus.OK);
    }
}

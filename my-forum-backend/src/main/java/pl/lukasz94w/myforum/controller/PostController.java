package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.NewPostContent;
import pl.lukasz94w.myforum.service.PostService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @PostMapping("/addPost")
    public ResponseEntity<HttpStatus> addPost(@Valid @RequestBody NewPostContent newPostContent, Authentication authentication) {
        return postService.addPost(newPostContent, authentication);
    }

    @GetMapping("/findPageablePostsByTopicId")
    public ResponseEntity<Map<String, Object>> findPageablePostsByTopicId(@RequestParam(defaultValue = "0") int page, @RequestParam final Long id) {
        return new ResponseEntity<>(this.postService.findPageablePostsByTopicId(page, id), HttpStatus.OK);
    }
}

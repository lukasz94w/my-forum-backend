package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.dto.PostDto;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/addPost")
    public ResponseEntity<PostDto> addPost(@RequestBody Post post) {
        return new ResponseEntity<>(this.postService.addPost(post), HttpStatus.CREATED);
    }

    @GetMapping("/getPostsByTopicId/{id}")
    public ResponseEntity<List<PostDto>> getPostsByTopicId(@PathVariable final Long id) {
        return new ResponseEntity<>(this.postService.getPostsByTopicId(id), HttpStatus.OK);
    }
}

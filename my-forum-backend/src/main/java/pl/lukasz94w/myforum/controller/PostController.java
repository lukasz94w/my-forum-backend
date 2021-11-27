package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.response.dto.PostDto;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.request.NewPostRequest;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;
import pl.lukasz94w.myforum.service.PostService;
import pl.lukasz94w.myforum.service.TopicService;
import pl.lukasz94w.myforum.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final TopicService topicService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, TopicService topicService, UserService userService) {
        this.postService = postService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole ('USER') or hasRole ('ADMIN')")
    @PostMapping("/addPost")
    public ResponseEntity<PostDto> addPost(@RequestBody NewPostRequest newPostRequest, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userService.findUserByUsername(userDetailsImpl.getUsername());
        Topic topicOfPost = topicService.findTopicById(newPostRequest.getTopicId());
        topicOfPost.setTimeOfActualization(LocalDateTime.now());
        Post newPost = new Post(newPostRequest.getContent(), topicOfPost, authenticatedUser);

        return new ResponseEntity<>(this.postService.addPost(newPost), HttpStatus.CREATED);
    }

    @GetMapping("/getPostsByTopicId/{id}")
    public ResponseEntity<List<PostDto>> getPostsByTopicId(@PathVariable final Long id) {
        return new ResponseEntity<>(this.postService.getPostsByTopicId(id), HttpStatus.OK);
    }
}

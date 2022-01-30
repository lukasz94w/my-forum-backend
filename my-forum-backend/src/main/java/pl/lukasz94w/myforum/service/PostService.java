package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.NewPostContent;
import pl.lukasz94w.myforum.response.PostDto;
import pl.lukasz94w.myforum.response.PostDto2;
import pl.lukasz94w.myforum.response.mapper.MapperDto;
import pl.lukasz94w.myforum.security.user.UserDetailsImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    public PostDto savePostForTestConstructor(Post post) {
        postRepository.save(post);
        return MapperDto.mapToPostDto(post);
    }

    public List<Object[]> countByCategoryList() {
        return postRepository.countPostsByCategories();
    }

    public List<Post> findLatestPostsInSummaryTopics(List<Long> topicIds) {
        return postRepository.findLatestPostsInEachOfLatestTopics(topicIds);
    }

    public Integer countPostByTopicCategory(Category category) {
        return postRepository.countPostByTopicCategory(category);
    }

    public Map<String, Object> findPageablePostsByTopicId(int page, Long id) {
        Pageable paging = PageRequest.of(page, 10, Sort.by("dateTime").ascending());
        Page<Post> pageablePosts = postRepository.findByTopicId(id, paging);

        Collection<PostDto> pageablePostsDto = pageablePosts.stream()
                .map(MapperDto::mapToPostDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pageablePosts", pageablePostsDto);
        response.put("currentPage", pageablePosts.getNumber());
        response.put("totalPosts", pageablePosts.getTotalElements());
        response.put("totalPages", pageablePosts.getTotalPages());

        return response;
    }

    public Integer countPostByTopic(Topic topic) {
        return postRepository.countPostByTopic(topic);
    }

    public ResponseEntity<HttpStatus> addPost(NewPostContent newPostContent, Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User authenticatedUser = userRepository.findByName(userDetailsImpl.getUsername());

        Topic topicOfPost = topicRepository.findTopicById(newPostContent.getTopicId());
        topicOfPost.setTimeOfActualization(LocalDateTime.now());
        topicRepository.save(topicOfPost);
        int numberOfPostsInTopic = postRepository.countPostByTopic(topicOfPost);
        Post newPost = new Post(newPostContent.getContent(), numberOfPostsInTopic + 1, topicOfPost, authenticatedUser);
        postRepository.save(newPost);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public Map<String, Object> searchInPosts(int page, String query) {
        Pageable paging = PageRequest.of(page, 10, Sort.by("dateTime").descending());
        Page<Post> pageablePosts = postRepository.findByContentContainsIgnoreCase(query, paging);

        Collection<PostDto2> pageablePostsDto2 = pageablePosts.stream()
                .map(MapperDto::mapToPostDto2)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pageablePosts", pageablePostsDto2);
        response.put("currentPage", pageablePosts.getNumber());
        response.put("totalPosts", pageablePosts.getTotalElements());
        response.put("totalPages", pageablePosts.getTotalPages());

        return response;
    }
}

package pl.lukasz94w.myforum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
import pl.lukasz94w.myforum.exception.exception.PostAddException;
import pl.lukasz94w.myforum.exception.reason.PostAddExceptionReason;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.NewPostContent;
import pl.lukasz94w.myforum.request.PostStatus;
import pl.lukasz94w.myforum.response.dto.PostDto;
import pl.lukasz94w.myforum.response.dto.PostDto2;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;
import pl.lukasz94w.myforum.security.auth.AuthorizedUserProvider;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final MapperDto mapperDto;
    private final AuthorizedUserProvider authorizedUserProvider;
    @Value("${pl.lukasz94w.pageableItemsNumber}")
    private int pageablePostsNumber;

    public void addPost(NewPostContent newPostContent) {
        User authenticatedUser = userRepository.findByName(authorizedUserProvider.getAuthorizedUserName());

        Topic topicOfPost = topicRepository
                .findById(newPostContent.getTopicId())
                .orElseThrow(() -> new PostAddException(PostAddExceptionReason.TOPIC_DOESNT_EXIST));

        if (topicOfPost.isClosed()) {
            throw new PostAddException(PostAddExceptionReason.TOPIC_WAS_CLOSED);
        }

        topicOfPost.setTimeOfActualization(LocalDateTime.now());
        topicRepository.save(topicOfPost);
        int numberOfPostsInTopic = postRepository.countPostByTopic(topicOfPost);
        Post newPost = new Post(newPostContent.getContent(), numberOfPostsInTopic + 1, topicOfPost, authenticatedUser);
        postRepository.save(newPost);
    }

    public void changeStatus(PostStatus postStatus) {
        Post post = postRepository.findPostById(postStatus.getPostId());
        post.setModerated(postStatus.isModerated());
        postRepository.save(post);
    }

    public Map<String, Object> findPageablePostsByTopicId(int page, Long id) {
        Pageable paging = PageRequest.of(page, pageablePostsNumber, Sort.by("dateTime").ascending());
        Page<Post> pageablePosts = postRepository.findByTopicId(id, paging);

        if (pageablePosts.getContent().size() == 0) {
            throw new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.POST_DOESNT_EXIST);
        }

        Collection<PostDto> pageablePostsDto = pageablePosts.stream()
                .map(mapperDto::mapToPostDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pageablePosts", pageablePostsDto);
        response.put("currentPage", pageablePosts.getNumber());
        response.put("totalPosts", pageablePosts.getTotalElements());
        response.put("totalPages", pageablePosts.getTotalPages());

        return response;
    }

    public Map<String, Object> searchInPosts(int page, String query) {
        Pageable paging = PageRequest.of(page, pageablePostsNumber, Sort.by("dateTime").descending());
        Page<Post> pageablePosts = postRepository.findByContentContainsIgnoreCase(query, paging);

        Collection<PostDto2> pageablePostsDto2 = pageablePosts.stream()
                .map(mapperDto::mapToPostDto2)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pageablePosts", pageablePostsDto2);
        response.put("currentPage", pageablePosts.getNumber());
        response.put("totalPosts", pageablePosts.getTotalElements());
        response.put("totalPages", pageablePosts.getTotalPages());

        return response;
    }
}

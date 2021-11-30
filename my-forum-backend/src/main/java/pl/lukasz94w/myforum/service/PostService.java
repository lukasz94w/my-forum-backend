package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.response.dto.PostDto;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostDto addPost(Post post) {
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
}

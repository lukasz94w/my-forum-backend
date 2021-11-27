package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.response.dto.PostDto;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.repository.PostRepository;

import java.util.List;
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

    public List<PostDto> getPostsByTopicId(Long id) {
        return postRepository.findAllByTopicId(id).
                stream()
                .map(MapperDto::mapToPostDto)
                .collect(Collectors.toList());
    }
}

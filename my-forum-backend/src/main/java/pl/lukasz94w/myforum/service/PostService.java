package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.lukasz94w.myforum.dto.PostDto;
import pl.lukasz94w.myforum.dtoConverter.DtoConverter;
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

    public PostDto createPost(Post post) {
        postRepository.save(post);
        return DtoConverter.convertPostToPostDto(post);
    }

    public List<PostDto> getPostsByTopicId(Long id) {
        return postRepository.findAllByTopicId(id).
                stream()
                .map(DtoConverter::convertPostToPostDto)
                .collect(Collectors.toList());
    }
}

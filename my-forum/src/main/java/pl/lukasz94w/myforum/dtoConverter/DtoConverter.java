package pl.lukasz94w.myforum.dtoConverter;

import pl.lukasz94w.myforum.dto.PostDto;
import pl.lukasz94w.myforum.model.Post;

public class DtoConverter {

    public static PostDto convertPostToPostDto(Post post) {
        return new PostDto(post.getId(), post.getTitle(), post.getContent());
    }
}

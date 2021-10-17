package pl.lukasz94w.myforum.dtoConverter;

import pl.lukasz94w.myforum.dto.PostDto;
import pl.lukasz94w.myforum.dto.TopicDto;
import pl.lukasz94w.myforum.dto.UserDto;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;

public class DtoConverter {

    public static TopicDto convertTopicToTopicDto(Topic topic) {
        return new TopicDto(topic.getId(), topic.getTitle(), topic.getContent());
    }

    public static PostDto convertPostToPostDto(Post post) {
        return new PostDto(post.getId(), post.getContent(), post.getTopic());
    }

    public static UserDto convertUserToUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword());
    }
}

package pl.lukasz94w.myforum.response.dto.mapper;

import pl.lukasz94w.myforum.response.dto.PostDto;
import pl.lukasz94w.myforum.response.dto.TopicDto;
import pl.lukasz94w.myforum.response.dto.UserDto;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;

public class MapperDto {

    public static TopicDto mapToTopicDto(Topic topic) {
        return new TopicDto(topic.getId(), topic.getTitle(), topic.getContent(), topic.getUser().getUsername(), topic.getDateTime());
    }

    public static PostDto mapToPostDto(Post post) {
        return new PostDto(post.getId(), post.getContent(), post.getUser().getUsername());
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword());
    }
}

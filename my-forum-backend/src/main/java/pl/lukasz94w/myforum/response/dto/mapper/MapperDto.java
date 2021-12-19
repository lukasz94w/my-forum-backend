package pl.lukasz94w.myforum.response.dto.mapper;

import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.response.dto.PostDto;
import pl.lukasz94w.myforum.response.dto.PostDto2;
import pl.lukasz94w.myforum.response.dto.TopicDto;
import pl.lukasz94w.myforum.response.dto.UserDto;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;

public class MapperDto {

    public static TopicDto mapToTopicDto(Topic topic) {
        return new TopicDto(topic.getId(), topic.getTitle(), topic.getUser().getName(), topic.getDateTime());
    }

    public static PostDto mapToPostDto(Post post) {

        ProfilePic profilePic = post.getUser().getProfilePic();
        byte[] profilePicData = null;
        if (profilePic != null) {
            profilePicData = profilePic.getData();
        }

        return new PostDto(post.getId(), post.getContent(), post.getUser().getName(), post.getDateTime(), profilePicData, post.getNumber());
    }

    public static PostDto2 mapToPostDto2(Post post) {
        return new PostDto2(post.getTopic().getId(), post.getTopic().getTitle(), post.getTopic().getCategory().toString(), post.getDateTime(), post.getContent(), post.getNumber());
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getPassword());
    }
}

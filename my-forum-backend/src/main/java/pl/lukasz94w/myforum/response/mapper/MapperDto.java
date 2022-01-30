package pl.lukasz94w.myforum.response.mapper;

import pl.lukasz94w.myforum.model.*;
import pl.lukasz94w.myforum.response.*;

public class MapperDto {

    public static TopicDto mapToTopicDto(Topic topic) {
        return new TopicDto(topic.getId(), topic.getTitle(), topic.getUser().getName(), topic.getDateTime());
    }

    public static PostDto mapToPostDto(Post post) {
        return new PostDto(post.getId(), post.getContent(), post.getUser().getName(), post.getDateTime(), getProfilePic(post.getUser()), post.getNumber());
    }

    public static PostDto2 mapToPostDto2(Post post) {
        // tutaj sprawdzac czy jest wymoderowany i nulle wstawiac w miejsce contentu jak byndzie
        return new PostDto2(post.getTopic().getId(), post.getTopic().getTitle(), post.getTopic().getCategory().toString(), post.getDateTime(), post.getContent(), post.getNumber());
    }

    public static TopicDto2 mapToTopicDto2(Topic topic) {
        return new TopicDto2(topic.getId(), topic.getTitle(), topic.getUser().getName(), topic.getDateTime(), topic.getCategory().toString());
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getName(), user.getEmail(), getProfilePic(user), user.getRegistered(), user.isAdmin(), user.isBanned());
    }

    public static UserDto mapToUserDto2(User user) {
        boolean isUserBanned = user.isBanned();
        if (isUserBanned) {
            return new UserDto2(user.getName(), user.getEmail(), getProfilePic(user),
                    user.getRegistered(), user.isAdmin(), user.isBanned(),
                    user.getBan().getDateAndTimeOfBan().toLocalDate(), user.getBan().getReasonOfBan());
        } else {
            return new UserDto2(user.getName(), user.getEmail(), getProfilePic(user),
                    user.getRegistered(), user.isAdmin(), user.isBanned(),
                    null, null);
        }
    }

    private static byte[] getProfilePic(User user) {
        ProfilePic profilePic = user.getProfilePic();
        byte[] profilePicData = null;
        if (profilePic != null) {
            profilePicData = profilePic.getData();
        }

        return profilePicData;
    }
}

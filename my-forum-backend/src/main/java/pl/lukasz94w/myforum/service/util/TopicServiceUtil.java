package pl.lukasz94w.myforum.service.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class TopicServiceUtil {

    public static List<Long> prepareNumbersOfEntries(List<Object[]> topicByCategoriesCount) {
        List<Long> fullListOfEntriesByCategoriesCount = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfEntriesByCategoriesCount.add(getCountOfEntriesInCategory(enumeratedCategory.toString(), topicByCategoriesCount));
        }

        return fullListOfEntriesByCategoriesCount;
    }

    public static long getCountOfEntriesInCategory(String category, List<Object[]> countedEntriesByCategory) {

        for (Object[] object : countedEntriesByCategory) {
            String entriesCategory = object[1].toString();
            long entriesCount = (long) object[0];
            if (entriesCategory.equals(category)) {
                return entriesCount;
            }
        }
        return 0;
    }

    public static Post findLatestPostInTopic(Topic topic, List<Post> listOfPosts) {

        for (Post post : listOfPosts) {
            if (post.getTopic().equals(topic))
                return post;
        }
        return null;
    }

    public static List<LastTopicActivity> prepareLastActivitiesInEachCategory(List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {
        List<LastTopicActivity> fullListOfLastActivitiesInCategory = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfLastActivitiesInCategory.add(findLastTopicInCategory(enumeratedCategory.toString(), latestTopicsInEachCategory, latestPostInEachOfLatestTopics));
        }

        return fullListOfLastActivitiesInCategory;
    }

    public static LastTopicActivity findLastTopicInCategory(String category, List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {

        for (Topic latestTopicInEachCategory : latestTopicsInEachCategory) {
            if (latestTopicInEachCategory.getCategory().toString().equals(category)) {
                return getLatestActivityInTopic(latestTopicInEachCategory, latestPostInEachOfLatestTopics);
            }
        }
        return null;
    }

    public static LastTopicActivity getLatestActivityInTopic(Topic foundTopic, List<Post> latestPostInEachOfLatestTopics) {
        LastTopicActivity lastTopicActivity;

        Post latestPost = findLatestPostInTopic(foundTopic, latestPostInEachOfLatestTopics);
        if (latestPost == null) {
            lastTopicActivity = new LastTopicActivity(foundTopic.getTitle(), foundTopic.getId(), foundTopic.getUser().getUsername(), getProfilePicData(foundTopic.getUser().getProfilePic()), foundTopic.getDateTime());
        } else {
            lastTopicActivity = new LastTopicActivity(foundTopic.getTitle(), foundTopic.getId(), latestPost.getUser().getUsername(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTimeOfPost());
        }

        return lastTopicActivity;
    }

    public static byte[] getProfilePicData(ProfilePic profilePic) {
        if (profilePic != null) {
            return profilePic.getData();
        }
        return null;
    }

    public static List<LastTopicActivity> prepareLastActivitiesInPageableTopics(List<Topic> listOfLatest10Topics, List<Post> listOfLatestPosts) {
        List<LastTopicActivity> fullListOfLastActivitiesInPageableTopics = new LinkedList<>();

        for (Topic topic : listOfLatest10Topics) {
            Post latestPost = findLatestPostInTopic(topic, listOfLatestPosts);
            if (latestPost == null) {
                fullListOfLastActivitiesInPageableTopics.add(new LastTopicActivity("", null, topic.getUser().getUsername(), getProfilePicData(topic.getUser().getProfilePic()), topic.getDateTime()));
            } else {
                fullListOfLastActivitiesInPageableTopics.add(new LastTopicActivity("", null, latestPost.getUser().getUsername(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTimeOfPost()));
            }
        }

        return fullListOfLastActivitiesInPageableTopics;
    }

    public static List<Long> prepareNumberOfPostsInPageableTopics(List<Topic> listOfLatest10Topics, List<Object[]> numberOfPostsInPageableTopics) {
        List<Long> numberOfPosts = new LinkedList<>();

        for (Topic latestTopic : listOfLatest10Topics) {
            numberOfPosts.add(countNumberOfPostsInTopic(latestTopic, numberOfPostsInPageableTopics));
        }

        return numberOfPosts;
    }

    private static Long countNumberOfPostsInTopic(Topic latestTopic, List<Object[]> numberOfPostsInPageableTopics) {

        for (Object[] object : numberOfPostsInPageableTopics) {
            Topic topic = (Topic) object[0];
            long numberOfPostsInTopic = (long) object[1];

            if (topic.equals(latestTopic))
                return numberOfPostsInTopic;
        }

        return 0L;
    }

    @AllArgsConstructor
    @Getter
    public static class LastTopicActivity {
        private String topicName;
        private Long topicId;
        private String userName;
        private byte[] userProfilePic;
        private LocalDateTime timeOfLastActivity;
    }
}

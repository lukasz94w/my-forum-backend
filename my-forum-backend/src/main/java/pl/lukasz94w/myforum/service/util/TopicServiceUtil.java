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

    public static List<LastActivityInCategory> prepareLastActivitiesInEachCategory(List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {
        List<LastActivityInCategory> fullListOfLastActivitiesInCategory = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfLastActivitiesInCategory.add(findLastTopicInCategory(enumeratedCategory.toString(), latestTopicsInEachCategory, latestPostInEachOfLatestTopics));
        }

        return fullListOfLastActivitiesInCategory;
    }

    public static LastActivityInCategory findLastTopicInCategory(String category, List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {

        for (Topic latestTopicInEachCategory : latestTopicsInEachCategory) {
            if (latestTopicInEachCategory.getCategory().toString().equals(category)) {
                return getLatestActivityInTopic(latestTopicInEachCategory, latestPostInEachOfLatestTopics);
            }
        }
        return null;
    }

    public static LastActivityInCategory getLatestActivityInTopic(Topic foundTopic, List<Post> latestPostInEachOfLatestTopics) {
        LastActivityInCategory lastActivityInCategory;

        Post latestPost = findLatestPostInTopic(foundTopic, latestPostInEachOfLatestTopics);
        if (latestPost == null) {
            lastActivityInCategory = new LastActivityInCategory(foundTopic.getTitle(), foundTopic.getId(), foundTopic.getUser().getName(), getProfilePicData(foundTopic.getUser().getProfilePic()), foundTopic.getDateTime(), 1);
        } else {
            lastActivityInCategory = new LastActivityInCategory(foundTopic.getTitle(), foundTopic.getId(), latestPost.getUser().getName(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTime(), latestPost.getNumber());
        }

        return lastActivityInCategory;
    }

    public static byte[] getProfilePicData(ProfilePic profilePic) {
        if (profilePic != null) {
            return profilePic.getData();
        }
        return null;
    }

    public static List<LastActivityInPageableTopic> prepareLastActivitiesInPageableTopics(List<Topic> listOfLatest10Topics, List<Post> listOfLatestPosts) {
        List<LastActivityInPageableTopic> lastActivitiesInPageableTopics = new LinkedList<>();

        for (Topic topic : listOfLatest10Topics) {
            Post latestPost = findLatestPostInTopic(topic, listOfLatestPosts);
            if (latestPost == null) {
                lastActivitiesInPageableTopics.add(new LastActivityInPageableTopic(topic.getUser().getName(), getProfilePicData(topic.getUser().getProfilePic()), topic.getDateTime(), 1));
            } else {
                lastActivitiesInPageableTopics.add(new LastActivityInPageableTopic(latestPost.getUser().getName(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTime(), latestPost.getNumber()));
            }
        }

        return lastActivitiesInPageableTopics;
    }

    public static List<Long> prepareNumberOfAnswersInPageableTopics(List<Topic> listOfLatest10Topics, List<Object[]> numberOfPostsInPageableTopics) {
        List<Long> numberOfPosts = new LinkedList<>();

        for (Topic latestTopic : listOfLatest10Topics) {
            numberOfPosts.add(countNumberOfAnswersInTopic(latestTopic, numberOfPostsInPageableTopics));
        }

        return numberOfPosts;
    }

    private static Long countNumberOfAnswersInTopic(Topic latestTopic, List<Object[]> numberOfPostsInPageableTopics) {

        for (Object[] object : numberOfPostsInPageableTopics) {
            Topic topic = (Topic) object[0];
            long numberOfPostsInTopic = (long) object[1];

            if (topic.equals(latestTopic))
                return numberOfPostsInTopic - 1;
        }

        return 0L;
    }

    @AllArgsConstructor
    @Getter
    public static class LastActivityInCategory {
        private String topicName;
        private Long topicId;
        private String userName;
        private byte[] userProfilePic;
        private LocalDateTime timeOfLastActivity;
        private int postNumber;
    }

    @AllArgsConstructor
    @Getter
    public static class LastActivityInPageableTopic {
        private String userName;
        private byte[] userProfilePic;
        private LocalDateTime timeOfLastActivity;
        private int postNumber;
    }

}

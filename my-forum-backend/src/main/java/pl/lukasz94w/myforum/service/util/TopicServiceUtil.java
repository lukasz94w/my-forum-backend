package pl.lukasz94w.myforum.service.util;

import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.ProfilePic;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.response.dto.LastActivityInCategoryDto;
import pl.lukasz94w.myforum.response.dto.LastActivityInPageableTopicDto;

import java.util.LinkedList;
import java.util.List;

@Component
public class TopicServiceUtil {

    public List<Long> prepareNumbersOfEntries(List<Object[]> topicByCategoriesCount) {
        List<Long> fullListOfEntriesByCategoriesCount = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfEntriesByCategoriesCount.add(getCountOfEntriesInCategory(enumeratedCategory.toString(), topicByCategoriesCount));
        }

        return fullListOfEntriesByCategoriesCount;
    }

    private long getCountOfEntriesInCategory(String category, List<Object[]> countedEntriesByCategory) {

        for (Object[] object : countedEntriesByCategory) {
            String entriesCategory = object[1].toString();
            long entriesCount = (long) object[0];
            if (entriesCategory.equals(category)) {
                return entriesCount;
            }
        }
        return 0;
    }

    private Post findLatestPostInTopic(Topic topic, List<Post> listOfPosts) {

        for (Post post : listOfPosts) {
            if (post.getTopic().equals(topic))
                return post;
        }
        return null;
    }

    public List<LastActivityInCategoryDto> prepareLastActivitiesInEachCategory(List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {
        List<LastActivityInCategoryDto> fullListOfLastActivitiesInCategory = new LinkedList<>();

        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            fullListOfLastActivitiesInCategory.add(findLastTopicInCategory(enumeratedCategory.toString(), latestTopicsInEachCategory, latestPostInEachOfLatestTopics));
        }

        return fullListOfLastActivitiesInCategory;
    }

    private LastActivityInCategoryDto findLastTopicInCategory(String category, List<Topic> latestTopicsInEachCategory, List<Post> latestPostInEachOfLatestTopics) {

        for (Topic latestTopicInEachCategory : latestTopicsInEachCategory) {
            if (latestTopicInEachCategory.getCategory().toString().equals(category)) {
                return getLatestActivityInTopic(latestTopicInEachCategory, latestPostInEachOfLatestTopics);
            }
        }
        return null;
    }

    private LastActivityInCategoryDto getLatestActivityInTopic(Topic foundTopic, List<Post> latestPostInEachOfLatestTopics) {
        LastActivityInCategoryDto lastActivityInCategoryDto;

        Post latestPost = findLatestPostInTopic(foundTopic, latestPostInEachOfLatestTopics);
        if (latestPost == null) {
            lastActivityInCategoryDto = new LastActivityInCategoryDto(foundTopic.getTitle(), foundTopic.getId(), foundTopic.getUser().getName(), getProfilePicData(foundTopic.getUser().getProfilePic()), foundTopic.getDateTime(), 1);
        } else {
            lastActivityInCategoryDto = new LastActivityInCategoryDto(foundTopic.getTitle(), foundTopic.getId(), latestPost.getUser().getName(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTime(), latestPost.getNumber());
        }

        return lastActivityInCategoryDto;
    }

    private byte[] getProfilePicData(ProfilePic profilePic) {
        if (profilePic != null) {
            return profilePic.getData();
        }
        return null;
    }

    public List<LastActivityInPageableTopicDto> prepareLastActivitiesInPageableTopics(List<Topic> listOfLatest10Topics, List<Post> listOfLatestPosts) {
        List<LastActivityInPageableTopicDto> lastActivitiesInPageableTopics = new LinkedList<>();

        for (Topic topic : listOfLatest10Topics) {
            Post latestPost = findLatestPostInTopic(topic, listOfLatestPosts);
            if (latestPost == null) {
                lastActivitiesInPageableTopics.add(new LastActivityInPageableTopicDto(topic.getUser().getName(), getProfilePicData(topic.getUser().getProfilePic()), topic.getDateTime(), 1));
            } else {
                lastActivitiesInPageableTopics.add(new LastActivityInPageableTopicDto(latestPost.getUser().getName(), getProfilePicData(latestPost.getUser().getProfilePic()), latestPost.getDateTime(), latestPost.getNumber()));
            }
        }

        return lastActivitiesInPageableTopics;
    }

    public List<Long> prepareNumberOfAnswersInPageableTopics(List<Topic> listOfLatest10Topics, List<Object[]> numberOfPostsInPageableTopics) {
        List<Long> numberOfPosts = new LinkedList<>();

        for (Topic latestTopic : listOfLatest10Topics) {
            numberOfPosts.add(countNumberOfAnswersInTopic(latestTopic, numberOfPostsInPageableTopics));
        }

        return numberOfPosts;
    }

    private Long countNumberOfAnswersInTopic(Topic latestTopic, List<Object[]> numberOfPostsInPageableTopics) {

        for (Object[] object : numberOfPostsInPageableTopics) {
            Topic topic = (Topic) object[0];
            long numberOfPostsInTopic = (long) object[1];

            if (topic.equals(latestTopic))
                return numberOfPostsInTopic - 1;
        }

        return 0L;
    }

}

package pl.lukasz94w.myforum.response;

import lombok.Getter;
import lombok.Setter;
import pl.lukasz94w.myforum.model.Topic;

import java.util.List;

@Getter
@Setter
public class SummaryResponse {

    private long totalGSub;
    private long tProg = 0;
    private long pProg = 0;
    private long tSport = 0;
    private long pSport = 0;
    private long tElect = 0;
    private long pElect = 0;
    private long tCar = 0;
    private long pCar = 0;

    private long totalOSub;
    private long tIntro = 0;
    private long pIntro = 0;
    private long tAdver = 0;
    private long pAdver = 0;
    private long tPers = 0;
    private long pPers = 0;

    private Topic latestTopProg;
    private Topic latestTopSport;
    private Topic latestTopElect;
    private Topic latestTopCar;

    private Topic latestTopIntro;
    private Topic latestTopAdver;
    private Topic latestTopPers;

    public SummaryResponse(List<Object[]> topicByCategoriesCount, List<Object[]> postByCategoriesCount, List<Topic> latestTopicInEachCategory) {
        prepareTopicsCount(topicByCategoriesCount);
        preparePostsCount(postByCategoriesCount);
        prepareTotalCounts();
        prepareLatestTopics(latestTopicInEachCategory);
    }

    private void prepareTopicsCount(List<Object[]> topicByCategoriesCount) {

        for (Object[] topicObject : topicByCategoriesCount) {

            String categoryName = topicObject[1].toString();
            long numberOfTopicsInThatCategory = (long) topicObject[0];

            switch (categoryName) {
                case "programming": {
                    tProg = numberOfTopicsInThatCategory;
                    break;
                }
                case "sport": {
                    tSport = numberOfTopicsInThatCategory;
                    break;
                }
                case "electronic": {
                    tElect = numberOfTopicsInThatCategory;
                    break;
                }
                case "car": {
                    tCar = numberOfTopicsInThatCategory;
                    break;
                }
                case "introduction": {
                    tIntro = numberOfTopicsInThatCategory;
                    break;
                }
                case "advertisement": {
                    tAdver = numberOfTopicsInThatCategory;
                    break;
                }
                case "personallife": {
                    tPers = numberOfTopicsInThatCategory;
                    break;
                }
            }
        }
    }

    private void preparePostsCount(List<Object[]> postByCategoriesCount) {

        for (Object[] postObject : postByCategoriesCount) {

            String categoryName = postObject[1].toString();
            long numberOfPostsInThatCategory = (long) postObject[0];

            switch (categoryName) {
                case "programming": {
                    pProg = numberOfPostsInThatCategory;
                    break;
                }
                case "sport": {
                    pSport = numberOfPostsInThatCategory;
                    break;
                }
                case "electronic": {
                    pElect = numberOfPostsInThatCategory;
                    break;
                }
                case "car": {
                    pCar = numberOfPostsInThatCategory;
                    break;
                }
                case "introduction": {
                    pIntro = numberOfPostsInThatCategory;
                    break;
                }
                case "advertisement": {
                    pAdver = numberOfPostsInThatCategory;
                    break;
                }
                case "personallife": {
                    pPers = numberOfPostsInThatCategory;
                    break;
                }
            }
        }
    }

    private void prepareTotalCounts() {
        totalGSub = tProg + pProg + tSport + pSport + tElect + pElect + tCar + pCar;
        totalOSub = tIntro + pIntro + tAdver + pAdver + tPers + pPers;
    }


    private void prepareLatestTopics(List<Topic> latestTopicInEachCategory) {
        for (Topic topic : latestTopicInEachCategory) {
            String categoryOfTopic = topic.getCategory().toString();

            switch(categoryOfTopic) {
                case "programming":
                {
                    latestTopProg = topic;
                    break;
                }
                case "sport":
                {
                    latestTopSport = topic;
                    break;
                }
                case "electronic": {
                    latestTopElect = topic;
                    break;
                }
                case "car": {
                    latestTopCar = topic;
                    break;
                }
                case "introduction": {
                    latestTopIntro = topic;
                    break;
                }
                case "advertisement": {
                    latestTopAdver = topic;
                    break;
                }
                case "personallife": {
                    latestTopPers = topic;
                    break;
                }
            }
        }
    }
}

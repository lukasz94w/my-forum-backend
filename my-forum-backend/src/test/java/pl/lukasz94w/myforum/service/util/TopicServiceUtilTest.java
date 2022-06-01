package pl.lukasz94w.myforum.service.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.response.dto.LastActivityInCategoryDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TopicServiceUtilTest {

    @Autowired
    TopicServiceUtil topicServiceUtil;

    @Test
    void shouldCountNumberOfEntriesWithAllCategoriesList() {
        List<Object[]> unsortedNumberOfEntriesByCategory = prepareListWithAllCategories();

        List<Long> sortedNumberOfEntriesFromTestingMethod = topicServiceUtil.prepareNumbersOfEntries(unsortedNumberOfEntriesByCategory);
        // categories order in enum class: programming, sport, electronics, car, introduction, advertisement, personal
        List<Long> expectedSortedList = List.of(432L, 24L, 93L, 3231L, 824L, 3213L, 31L);

        assertEquals(expectedSortedList, sortedNumberOfEntriesFromTestingMethod);
    }

    @Test
    void shouldCountNumberOfEntriesWithNotAllCategoriesList() {
        List<Object[]> unsortedNotFullListOfNumberOfEntriesByCategory = prepareListWithNotAllCategories();

        List<Long> sortedNumberOfEntriesFromTestingMethod = topicServiceUtil.prepareNumbersOfEntries(unsortedNotFullListOfNumberOfEntriesByCategory);
        // categories order in enum class: programming, sport, electronics, car, introduction, advertisement, personal
        List<Long> expectedSortedList = List.of(0L, 61L, 0L, 452L, 824L, 123L, 932L);

        assertEquals(expectedSortedList, sortedNumberOfEntriesFromTestingMethod);
    }

    @Test
    void shouldReturnLastActivitiesInEachCategory() {
        User user1 = new User();
        user1.setName("user1");
        User user2 = new User();
        user2.setName("user2");

        Topic sportTopic = new Topic("Sport topic", user1, new Category(EnumeratedCategory.SPORT));
        Topic programmingTopic = new Topic("Programming topic", user1, new Category(EnumeratedCategory.PROGRAMMING));
        Topic carTopic = new Topic("Car topic", user1, new Category(EnumeratedCategory.CAR));
        Topic personalTopic = new Topic("Personal topic", user1, new Category(EnumeratedCategory.PERSONAL));

        Post latestPostForProgrammingTopic = new Post("Content programming", 54, programmingTopic, user1);
        Post latestPostForSportTopic = new Post("Content sport", 3, sportTopic, user1);
        Post latestPostForCarTopic = new Post("Content car", 17, carTopic, user2);

        List<Topic> latestTopicsInEachCategory = List.of(sportTopic, programmingTopic, carTopic, personalTopic);
        List<Post> latestPostInLatestTopics = List.of(latestPostForCarTopic, latestPostForSportTopic, latestPostForProgrammingTopic);

        LastActivityInCategoryDto lastActivityInProgramming = new LastActivityInCategoryDto(
                programmingTopic.getTitle(), null, latestPostForProgrammingTopic.getUser().getName(),
                null, latestPostForProgrammingTopic.getDateTime(), 54);
        LastActivityInCategoryDto lastActivityInSport = new LastActivityInCategoryDto(
                sportTopic.getTitle(), null, latestPostForSportTopic.getUser().getName(),
                null, latestPostForSportTopic.getDateTime(), 3);
        LastActivityInCategoryDto lastActivityInElectronics = null;
        LastActivityInCategoryDto lastActivityInCar = new LastActivityInCategoryDto(
                carTopic.getTitle(), null, latestPostForCarTopic.getUser().getName(),
                null, latestPostForCarTopic.getDateTime(), 17);
        LastActivityInCategoryDto lastActivityInIntroduction = null;
        LastActivityInCategoryDto lastActivityInAdvertisement = null;
        LastActivityInCategoryDto lastActivityInPersonal = new LastActivityInCategoryDto(
                personalTopic.getTitle(), null, personalTopic.getUser().getName(),
                null, personalTopic.getDateTime(), 1);

        List<LastActivityInCategoryDto> expectedListOfLatestActivities = new LinkedList<>();
        expectedListOfLatestActivities.add(lastActivityInProgramming);
        expectedListOfLatestActivities.add(lastActivityInSport);
        expectedListOfLatestActivities.add(lastActivityInElectronics);
        expectedListOfLatestActivities.add(lastActivityInCar);
        expectedListOfLatestActivities.add(lastActivityInIntroduction);
        expectedListOfLatestActivities.add(lastActivityInAdvertisement);
        expectedListOfLatestActivities.add(lastActivityInPersonal);

        List<LastActivityInCategoryDto> lastActivitiesFromTestedMethod = topicServiceUtil.prepareLastActivitiesInEachCategory(latestTopicsInEachCategory, latestPostInLatestTopics);

        for (int i = 0; i < lastActivitiesFromTestedMethod.size(); i++) {
            assertTrue(compareActivities(expectedListOfLatestActivities.get(i), lastActivitiesFromTestedMethod.get(i)));
        }
    }

    @Test
    void shouldPrepareNumberOfAnswersInTopics() {
        User user = new User();
        Topic topic1 = new Topic("Topic 1", user, new Category(EnumeratedCategory.SPORT));
        Topic topic2 = new Topic("Topic 2", user, new Category(EnumeratedCategory.PROGRAMMING));
        Topic topic3 = new Topic("Topic 3", user, new Category(EnumeratedCategory.CAR));
        Topic topic4 = new Topic("Topic 4", user, new Category(EnumeratedCategory.PERSONAL));
        Topic topic5 = new Topic("Topic 5", user, new Category(EnumeratedCategory.SPORT));
        Topic topic6 = new Topic("Topic 6", user, new Category(EnumeratedCategory.PROGRAMMING));
        Topic topic7 = new Topic("Topic 7", user, new Category(EnumeratedCategory.CAR));
        Topic topic8 = new Topic("Topic 8", user, new Category(EnumeratedCategory.PERSONAL));
        Topic topic9 = new Topic("Topic 9", user, new Category(EnumeratedCategory.SPORT));
        Topic topic10 = new Topic("Topic 10", user, new Category(EnumeratedCategory.PROGRAMMING));
        List<Topic> topics = List.of(topic1, topic2, topic3, topic4, topic5, topic6, topic7, topic8, topic9, topic10);

        List<Object[]> numberOfPostsInPageableTopics = new LinkedList<>();
        numberOfPostsInPageableTopics.add(new Object[] {topic6, 27L});
        numberOfPostsInPageableTopics.add(new Object[] {topic2, 54L});
        numberOfPostsInPageableTopics.add(new Object[] {topic3, 324L});
        numberOfPostsInPageableTopics.add(new Object[] {topic9, 3L});
        numberOfPostsInPageableTopics.add(new Object[] {topic4, 222L});

        Collection<Long> numberOfAnswersInPageableTopics = topicServiceUtil.prepareNumberOfAnswersInPageableTopics(topics, numberOfPostsInPageableTopics);
        Collection<Long> expectedListOfNumberOfAnswers = new LinkedList<>(Arrays.asList(0L, 53L, 323L, 221L, 0L, 26L, 0L, 0L, 2L, 0L));

        assertEquals(expectedListOfNumberOfAnswers, numberOfAnswersInPageableTopics);
    }

    List<Object[]> prepareListWithAllCategories() {
        List<Object[]> unsortedNumberOfEntriesByCategory = new LinkedList<>();
        unsortedNumberOfEntriesByCategory.add(new Object[]{3213L, "advertisement"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{824L, "introduction"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{93L, "electronics"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{31L, "personal"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{24L, "sport"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{3231L, "car"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{432L, "programming"});
        return unsortedNumberOfEntriesByCategory;
    }

    List<Object[]> prepareListWithNotAllCategories() {
        List<Object[]> unsortedNumberOfEntriesByCategory = new LinkedList<>();
        unsortedNumberOfEntriesByCategory.add(new Object[]{61L, "sport"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{824L, "introduction"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{932L, "personal"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{123L, "advertisement"});
        unsortedNumberOfEntriesByCategory.add(new Object[]{452L, "car"});
        return unsortedNumberOfEntriesByCategory;
    }

    boolean compareActivities(LastActivityInCategoryDto expectedLastActivity, LastActivityInCategoryDto lastActivityFromTestedMethod) {
        if (expectedLastActivity == null && lastActivityFromTestedMethod == null) {
            return true;
        } else {
            boolean areTopicNamesEqual = expectedLastActivity.getTopicName().equals(lastActivityFromTestedMethod.getTopicName());
            boolean areUserNamesEqual = expectedLastActivity.getUserName().equals(lastActivityFromTestedMethod.getUserName());
            boolean areDateTimesEqual = expectedLastActivity.getTimeOfLastActivity().equals(lastActivityFromTestedMethod.getTimeOfLastActivity());
            boolean arePostNumbersEqual = expectedLastActivity.getPostNumber() == lastActivityFromTestedMethod.getPostNumber();
            return areTopicNamesEqual && areUserNamesEqual && areDateTimesEqual && arePostNumbersEqual;
        }
    }
}
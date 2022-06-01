package pl.lukasz94w.myforum.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Role;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TopicRepositoryTest {

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeAll // instead of creating test data using java it could be SQL script used (@Sql annotation)
    void prepareTestData() {
        User user = getUserForTest();
        List<Category> categories = getCategoriesForTest();
        saveTopicsInAllCategories("Newest topic by date time", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS), categories, user);
        saveTopicsInAllCategories("Some older topic", LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.DAYS), categories, user);
        saveTopicsInAllCategories("Another older topic", LocalDateTime.now().minusDays(5).truncatedTo(ChronoUnit.DAYS), categories, user);
        saveTopicsInAllCategories("Oldest topic", LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.DAYS), categories, user);
    }

    @Test
    void testIfFindLatestTopicInEachCategory() {
        // when
        List<Topic> topics = topicRepository.findLatestTopicInEachCategory();

        // then
        for (Topic topic : topics) {
            assertEquals("Newest topic by date time", topic.getTitle());
            assertEquals(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS), topic.getTimeOfActualization());
        }
        assertEquals(7, topics.size());
    }

    @Test
    void shouldCountCorrectlyTopicsByCategories() {
        // when
        List<Object[]> topicsByCategories = topicRepository.countTopicsByCategories();

        // then
        for (Object[] objects : topicsByCategories) {
            long numberOfTopicsInCategory = (long) objects[0];
            assertEquals(4, numberOfTopicsInCategory);
        }
        assertEquals(7, topicsByCategories.size());
    }

    User getUserForTest() {
        Set<Role> roles = new HashSet<>(List.of(roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER)));
        User user = new User("someUser", "someUser@gmail.com", "password", roles);
        userRepository.save(user);
        return user;
    }

    List<Category> getCategoriesForTest() {
        List<Category> categories = new LinkedList<>();
        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            categories.add(categoryRepository.findByEnumeratedCategory(enumeratedCategory));
        }
        return categories;
    }

    void saveTopicsInAllCategories(String topicTitle, LocalDateTime dateTimeOfTopic, List<Category> categories, User user) {
        for (Category category : categories) {
            Topic newestTopic = new Topic(topicTitle, user, category);
            newestTopic.setTimeOfActualization(dateTimeOfTopic);
            topicRepository.save(newestTopic);
        }
    }
}
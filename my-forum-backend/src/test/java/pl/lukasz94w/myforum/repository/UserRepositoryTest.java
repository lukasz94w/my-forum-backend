package pl.lukasz94w.myforum.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PostRepository postRepository;

    Map<Long, Long> userIdsWithNumberOfPosts;
    Map<Long, Long> userIdsWithNumberOfTopics;

    @BeforeAll
    void prepareTestData() {
        userIdsWithNumberOfPosts = new LinkedHashMap<>();
        userIdsWithNumberOfTopics = new LinkedHashMap<>();

        User user1 = prepareUser("user1");
        User user2 = prepareUser("user2");
        User user3 = prepareUser("user3");
        User admin = prepareUser("admin");

        Category sport = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.SPORT);

        prepareRandomNumberOfTopicsAndPostsFor(user1, sport);
        prepareRandomNumberOfTopicsAndPostsFor(user2, sport);
        prepareRandomNumberOfTopicsAndPostsFor(user3, sport);
        prepareRandomNumberOfTopicsAndPostsFor(admin, sport);
    }

    @Test
    void shouldCountPostsByUser() {
        // given
        List<Long> userIds = new ArrayList<>(userIdsWithNumberOfPosts.keySet());

        // when
        List<Object[]> usersWithNumberOfPosts = userRepository.countPostsInPageableUsers(userIds);

        // then
        for (Object[] userWithNumberOfPosts : usersWithNumberOfPosts) {
            User foundUser = (User) userWithNumberOfPosts[0];
            Long foundNumberOfPosts = (Long) userWithNumberOfPosts[1];
            Long idOfUserFromMap = getIdOfUserFromMap(foundUser);
            assertEquals(userIdsWithNumberOfPosts.get(idOfUserFromMap), foundNumberOfPosts);
        }
    }

    @Test
    void shouldCountTopicsByUser() {
        // given
        List<Long> userIdWithNoTopicsAndPosts = new ArrayList<>(userIdsWithNumberOfTopics.keySet());

        // when
        List<Object[]> usersWithNumberOfTopics = userRepository.countTopicsInPageableUsers(userIdWithNoTopicsAndPosts);

        // then
        for (Object[] userWithNumberOfTopics : usersWithNumberOfTopics) {
            User foundUser = (User) userWithNumberOfTopics[0];
            Long foundNumberOfPosts = (Long) userWithNumberOfTopics[1];
            Long idOfUserFromMap = getIdOfUserFromMap(foundUser);
            assertEquals(userIdsWithNumberOfTopics.get(idOfUserFromMap), foundNumberOfPosts);
        }
    }

    @Test
    void shouldNotFindAnyTopicsAndPostsForUser() {
        // given
        User user4 = userRepository.findByName("user4"); // this user will not have any topics and posts

        // when
        List<Object[]> userWithNumberOfTopics = userRepository.countTopicsInPageableUsers(List.of(user4.getId()));
        List<Object[]> userWithNumberOfPosts = userRepository.countPostsInPageableUsers(List.of(user4.getId()));

        // then
        assertEquals(0, userWithNumberOfTopics.size());
        assertEquals(0, userWithNumberOfPosts.size());
    }

    User prepareUser(String userName) {
        User user = userRepository.findByName(userName);
        userIdsWithNumberOfPosts.put(user.getId(), null);
        userIdsWithNumberOfTopics.put(user.getId(), null);
        return user;
    }

    void prepareRandomNumberOfTopicsAndPostsFor(User user, Category category) {
        long numberOfTopicsForGivenUser = 0;
        long numberOfPostsForGivenUser = 0;
        for (int i = 0; i < getRandomNumberOfLoops(); i++) {
            Topic topic = topicRepository.save(new Topic("Topic title", user, category));
            postRepository.save(new Post("Some content", 1, topic, user));
            postRepository.save(new Post("Some content", 2, topic, user));
            postRepository.save(new Post("Some content", 3, topic, user));
            numberOfTopicsForGivenUser += 1;
            numberOfPostsForGivenUser += 3;
        }
        userIdsWithNumberOfTopics.put(user.getId(), numberOfTopicsForGivenUser);
        userIdsWithNumberOfPosts.put(user.getId(), numberOfPostsForGivenUser);
    }

    int getRandomNumberOfLoops() {
        Random random = new Random();
        return random.ints(1, 10)
                .findFirst()
                .getAsInt();
    }

    Long getIdOfUserFromMap(User foundUser) {
        return userIdsWithNumberOfPosts.keySet().stream()
                .filter(id -> id.equals(foundUser.getId()))
                .findFirst()
                .get();
    }
}
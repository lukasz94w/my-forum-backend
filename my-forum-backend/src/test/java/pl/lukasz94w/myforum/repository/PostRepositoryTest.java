package pl.lukasz94w.myforum.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.lukasz94w.myforum.model.*;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PostRepositoryTest {

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

    @Test
    @Transactional
    void shouldCountPostsByCategories() {
        // given
        User user = getUserForTest();
        List<Category> categories = getCategoriesForTest();
        Map<String, Long> generatedNumberOfPostsInEachCategory = saveRandomNumberOfPostsInEachCategory(user, categories);

        // when
        List<Object[]> numberOfPostsInCategories = postRepository.countPostsByCategories();

        // then
        for (Object[] numberOfPostsInCategory : numberOfPostsInCategories) {
            Long foundNumberOfPosts = (Long) numberOfPostsInCategory[0];
            String foundCategoryName = numberOfPostsInCategory[1].toString();
            assertEquals(generatedNumberOfPostsInEachCategory.get(foundCategoryName), foundNumberOfPosts);
        }
        assertEquals(EnumeratedCategory.values().length, numberOfPostsInCategories.size());
    }

    @Test
    @Transactional
    void shouldCountPostsByOnlyOneExistingCategory() {
        // given
        User user = getUserForTest();
        Map<String, Long> generatedNumberOfPostsInProgramming = saveRandomNumberOfPostsInProgramming(user);

        // when
        List<Object[]> numberOfPostsInProgramming = postRepository.countPostsByCategories();

        // then
        Long foundNumberOfPosts = (Long) numberOfPostsInProgramming.get(0)[0];
        String foundCategoryName = numberOfPostsInProgramming.get(0)[1].toString();
        assertEquals("programming", foundCategoryName);
        assertEquals(generatedNumberOfPostsInProgramming.get("programming"), foundNumberOfPosts);
        assertEquals(1, numberOfPostsInProgramming.size());
    }

    @Test
    @Transactional
    void shouldFindLatestPostsInTopics() {
        // given
        User user = getUserForTest();
        List<Long> topicIds = saveSomeRandomPostsWithTopics(user);

        // when
        List<Post> latestPostsInEachOfFoundTopics = postRepository.findLatestPostsInEachOfLatestTopics(topicIds);

        // then
        for (Post foundPost : latestPostsInEachOfFoundTopics) {
            assertEquals("Latest post content", foundPost.getContent());
        }
    }

    @Test
    @Transactional
    void shouldCountPostsInTopics() {
        // given
        User user = getUserForTest();
        List<Long> topicIds = saveSomeRandomPostsWithTopics(user);
        int generatedNumberOfPostsInTopic = 3; // there are 3 posts in each topic

        // when
        List<Object[]> topicsWithNumberOfPosts = postRepository.countPostsInPageableTopics(topicIds);

        // then
        for (Object[] foundTopicWithNumberOfPosts : topicsWithNumberOfPosts) {
            long foundNumberOfPostsInTopic = (long) foundTopicWithNumberOfPosts[1];
            assertEquals(generatedNumberOfPostsInTopic, foundNumberOfPostsInTopic);
        }
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

    Map<String, Long> saveRandomNumberOfPostsInEachCategory(User user, List<Category> categories) {
        Map<String, Long> numberOfPostsInCategories = new LinkedHashMap<>();
        for (EnumeratedCategory enumeratedCategory : EnumeratedCategory.values()) {
            numberOfPostsInCategories.putAll(saveRandomNumberOfPostsInCategory(user, getCategoryByName(enumeratedCategory.toString(), categories)));
        }
        return numberOfPostsInCategories;
    }

    Map<String, Long> saveRandomNumberOfPostsInCategory(User user, Category category) {
        long numberOfPostsInGivenCategory = 0L;

        for (int i = 0; i < getRandomNumberOfLoops(); i++) {
            saveSingleTopic(user, category);
            numberOfPostsInGivenCategory += 3;
        }

        return Collections.singletonMap(category.toString(), numberOfPostsInGivenCategory);
    }

    long saveSingleTopic(User user, Category category) {
        Topic topic = topicRepository.save(new Topic("Some topic", user, category));
        postRepository.save(new Post("First post content", 1, topic, user));
        postRepository.save(new Post("Second post content", 2, topic, user));
        postRepository.save(new Post("Latest post content", 3, topic, user));
        return topic.getId();
    }

    Category getCategoryByName(String categoryName, List<Category> categories) {
        return categories.stream()
                .filter(category -> category.toString().equals(categoryName))
                .findFirst()
                .get();
    }

    int getRandomNumberOfLoops() {
        Random random = new Random();
        return random.ints(1, 10)
                .findFirst()
                .getAsInt();
    }

    Map<String, Long> saveRandomNumberOfPostsInProgramming(User user) {
        Category programming = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.PROGRAMMING);
        return new LinkedHashMap<>(saveRandomNumberOfPostsInCategory(user, programming));
    }

    List<Long> saveSomeRandomPostsWithTopics(User user) {
        List<Long> topicIds = new LinkedList<>();
        for (int i = 0; i < getRandomNumberOfLoops(); i++) {
            topicIds.add(saveSingleTopic(user, categoryRepository.findByEnumeratedCategory(EnumeratedCategory.INTRODUCTION)));
        }
        return topicIds;
    }
}
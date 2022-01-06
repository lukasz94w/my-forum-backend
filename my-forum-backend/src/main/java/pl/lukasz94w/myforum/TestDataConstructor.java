package pl.lukasz94w.myforum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.model.*;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.RoleRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.service.PostService;
import pl.lukasz94w.myforum.service.UserService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class TestDataConstructor {

    private final TopicRepository topicRepository;
    private final PostService postService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataConstructor(TopicRepository topicRepository, PostService postService, UserService userService, RoleRepository roleRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.topicRepository = topicRepository;
        this.postService = postService;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void createTestData() {
        System.out.println("Creating test data...");

        //ROLES
        roleRepository.save(new Role(EnumeratedRole.ROLE_USER));
        roleRepository.save(new Role(EnumeratedRole.ROLE_ADMIN));

        Role userRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER);
        Role adminRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_ADMIN);

        Set<Role> rolesForUser = new HashSet<>() {{
            add(userRole);
        }};
        Set<Role> rolesForAdmin = new HashSet<>() {{
            add(userRole);
            add(adminRole);
        }};

        //USERS
        User user1 = new User("user1", "user1@gmail.com", passwordEncoder.encode("user1"), rolesForUser);
        user1.setEnabled(true);
        userService.saveUser(user1);
        User user2 = new User("user2", "user2@gmail.com", passwordEncoder.encode("user2"), rolesForUser);
        user2.setEnabled(true);
        userService.saveUser(user2);
        User user3 = new User("user3", "user3@gmail.com", passwordEncoder.encode("user3"), rolesForUser);
        user3.setEnabled(true);
        userService.saveUser(user3);
        User user4 = new User("user4", "user4@gmail.com", passwordEncoder.encode("user4"), rolesForUser);
        user4.setEnabled(true);
        userService.saveUser(user4);
        User user5 = new User("user5", "user5@gmail.com", passwordEncoder.encode("user5"), rolesForUser);
        user5.setEnabled(true);
        userService.saveUser(user5);
        User admin = new User("admin", "admin@gmail.com", passwordEncoder.encode("admin"), rolesForAdmin);
        admin.setEnabled(false);
        userService.saveUser(admin);
        User lukasz94w = new User("lukasz94w", "lukasz94w@wp.pl", passwordEncoder.encode("lukasz94w"), rolesForUser);
        lukasz94w.setEnabled(true);
        userService.saveUser(lukasz94w);

        //CATEGORIES
        categoryRepository.save(new Category(EnumeratedCategory.PROGRAMMING));
        categoryRepository.save(new Category(EnumeratedCategory.SPORT));
        categoryRepository.save(new Category(EnumeratedCategory.ELECTRONIC));
        categoryRepository.save(new Category(EnumeratedCategory.CAR));
        categoryRepository.save(new Category(EnumeratedCategory.INTRODUCTION));
        categoryRepository.save(new Category(EnumeratedCategory.ADVERTISEMENT));
        categoryRepository.save(new Category(EnumeratedCategory.PERSONAL));

        Category programming = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.PROGRAMMING);
        Category sport = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.SPORT);
        Category electronic = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.ELECTRONIC);

        //TOPICS AND POSTS
        for (int i = 0; i <= 1; i++) {
            Topic topic1 = new Topic("Kupno telewizora za 2000zl TOPIC 1", user1, electronic);
            topicRepository.save(topic1);
            createCommentsForTopic(topic1, user1, user2, user3, user4, user5, admin, user1);
            Topic topic2 = new Topic("Kupno telewizora za 5000zl", user2, programming);
            topicRepository.save(topic2);
            createCommentsForTopic(topic2, user1, user2, user3, user4, user5, admin, user2);
            Topic topic5 = new Topic("Topic 5", user2, sport);
            topicRepository.save(topic5);
            createCommentsForTopic(topic5, user1, user2, user3, user4, user5, admin, user2);
            Topic topic6 = new Topic("Kupno telewizora za 2000zl", user3, programming);
            topicRepository.save(topic6);
            createCommentsForTopic(topic6, user1, user2, user3, user4, user5, admin, user3);
            Topic topic7 = new Topic("Kupno telewizora za 5000zl", user4, programming);
            topicRepository.save(topic7);
            createCommentsForTopic(topic7, user1, user2, user3, user4, user5, admin, user4);
        }

        System.out.println("Data successfully created");
    }

    private void createCommentsForTopic(Topic topic, User user1, User user2, User user3, User user4, User user5, User admin, User author) {
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 1", 1, topic, author));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 1", 2, topic, user5));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest pierwszy komentarz do tematu nr 2", 3, topic, user4));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest jakis bardzo dlugi komentarz xd", 4, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 2", 5, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("Blabla", 6, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 1", 7, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 1", 8, topic, user3));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest pierwszy komentarz do tematu nr 2", 9, topic, admin));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 2", 10, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 2", 11, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 1", 12, topic, user3));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 1", 13, topic, user5));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest pierwszy komentarz do tematu nr 2", 14, topic, user4));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest jakis bardzo dlugi komentarz xd", 15, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 2", 16, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("Blabla", 17, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 1", 18, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 1", 19, topic, user3));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest pierwszy komentarz do tematu nr 2", 20, topic, admin));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 2", 21, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 2", 22, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 1", 23, topic, user3));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 1", 24, topic, user5));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest pierwszy komentarz do tematu nr 2", 25, topic, user4));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest jakis bardzo dlugi komentarz xd", 26, topic, user2));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 2", 27, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("Blabla", 28, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 1", 29, topic, user2));        wait(1);
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 1", 30, topic, user3));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest pierwszy komentarz do tematu nr 2", 31, topic, admin));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest drugi komentarz do tematu nr 2", 32, topic, user1));
        wait(1);
        postService.savePostForTestConstructor(new Post("To jest trzeci komentarz do tematu nr 2", 33, topic, user2));
        wait(1);
    }

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}

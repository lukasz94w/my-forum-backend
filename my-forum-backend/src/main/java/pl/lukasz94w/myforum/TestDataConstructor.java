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

        Role userRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Role adminRole = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Set<Role> rolesForUser = new HashSet<>() {{
            add(userRole);
        }};
        Set<Role> rolesForAdmin = new HashSet<>() {{
            add(userRole);
            add(adminRole);
        }};

        //USERS
        User user1 = new User("user1", "user1@gmail.com", passwordEncoder.encode("user1"), rolesForUser);
        userService.saveUser(user1);
        User user2 = new User("user2", "user2@gmail.com", passwordEncoder.encode("user2"), rolesForUser);
        userService.saveUser(user2);
        User user3 = new User("user3", "user3@gmail.com", passwordEncoder.encode("user3"), rolesForUser);
        userService.saveUser(user3);
        User user4 = new User("user4", "user4@gmail.com", passwordEncoder.encode("user4"), rolesForUser);
        userService.saveUser(user4);
        User user5 = new User("user5", "user5@gmail.com", passwordEncoder.encode("user5"), rolesForUser);
        userService.saveUser(user5);
        User admin = new User("admin", "admin@gmail.com", passwordEncoder.encode("admin"), rolesForAdmin);
        userService.saveUser(admin);

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

        for (int i = 0; i <= 250; i++) {

            Topic topic1 = new Topic("Kupno telewizora za 2000zl TOPIC 1", user1, electronic);
            topicRepository.save(topic1);
            Topic topic2 = new Topic("Kupno telewizora za 5000zl", user2, programming);
            topicRepository.save(topic2);
            Topic topic5 = new Topic("Topic 5", user2, sport);
            topicRepository.save(topic5);
            Topic topic6 = new Topic("Kupno telewizora za 2000zl", user3, programming);
            topicRepository.save(topic6);
            Topic topic7 = new Topic("Kupno telewizora za 5000zl", user4, programming);
            topicRepository.save(topic7);

            for (int j = 0; j < 20; j++) {
                postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 1", topic1, user1));
            }
            postService.addPost(new Post("To jest drugi komentarz do tematu nr 1", topic5, user3));
            postService.addPost(new Post("To jest trzeci komentarz do tematu nr 1", topic1, user5));
            postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 2", topic2, user4));
            postService.addPost(new Post("To jest drugi komentarz do tematu nr 2", topic1, user2));
            postService.addPost(new Post("To jest trzeci komentarz do tematu nr 2", topic2, user1));
            postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 1", topic6, user1));
            postService.addPost(new Post("To jest drugi komentarz do tematu nr 1", topic7, user2));
            postService.addPost(new Post("To jest trzeci komentarz do tematu nr 1", topic5, user3));
            postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 2", topic7, admin));
            postService.addPost(new Post("To jest drugi komentarz do tematu nr 2", topic6, user1));
            postService.addPost(new Post("To jest trzeci komentarz do tematu nr 2", topic5, user2));
        }

        System.out.println("Data successfully created");
    }
}
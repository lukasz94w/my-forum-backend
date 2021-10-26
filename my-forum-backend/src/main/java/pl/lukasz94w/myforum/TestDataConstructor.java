package pl.lukasz94w.myforum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.model.*;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.RoleRepository;
import pl.lukasz94w.myforum.service.PostService;
import pl.lukasz94w.myforum.service.TopicService;
import pl.lukasz94w.myforum.service.UserService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class TestDataConstructor {

    private final TopicService topicService;
    private final PostService postService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataConstructor(TopicService topicService, PostService postService, UserService userService, RoleRepository roleRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.topicService = topicService;
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
        User user = new User("user", "user@gmail.com", passwordEncoder.encode("user"), rolesForUser);
        userService.saveUser(user);
        User admin = new User("admin", "admin@gmail.com", passwordEncoder.encode("admin"), rolesForAdmin);
        userService.saveUser(admin);

        //CATEGORIES
        categoryRepository.save(new Category(EnumeratedCategory.PROGRAMMING));
        categoryRepository.save(new Category(EnumeratedCategory.SPORT));
        categoryRepository.save(new Category(EnumeratedCategory.ELECTRONIC));
        categoryRepository.save(new Category(EnumeratedCategory.CAR));
        categoryRepository.save(new Category(EnumeratedCategory.INTRODUCTION));
        categoryRepository.save(new Category(EnumeratedCategory.ADVERTISEMENT));
        categoryRepository.save(new Category(EnumeratedCategory.PERSONALLIFE));

        Category programming = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.PROGRAMMING);
        Category sport = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.SPORT);
        Category electronic = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.ELECTRONIC);

        //TOPICS AND POSTS
        Topic topic1 = new Topic("Kupno telewizora za 2000zl", "Cras vel sapien sagittis, vulputate felis sit amet, volutpat arcu. Cras euismod metus eros, ut lobortis elit egestas ut. Aliquam at posuere metus. In luctus nibh mi, non tincidunt tortor feugiat sed. Morbi non venenatis nunc. Aliquam posuere, lorem porttitor feugiat ultricies, sem urna venenatis metus, vitae ornare justo leo at libero. Aliquam fringilla lectus at ullamcorper venenatis. Sed et elementum sapienm sed quis ipsum.", admin, electronic);
        topicService.createTopic(topic1);

        postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 1", topic1, admin));
        postService.addPost(new Post("To jest drugi komentarz do tematu nr 1", topic1, admin));
        postService.addPost(new Post("To jest trzeci komentarz do tematu nr 1", topic1, admin));

        Topic topic2 = new Topic("Kupno telewizora za 5000zl", "Cras vel sapien sagittis, vulputate felis sit amet, volutpat arcu. Cras euismod metus eros, ut lobortis elit egestas ut. Aliquam at posuere metus. In luctus nibh mi, non tincidunt tortor feugiat sed. Morbi non venenatis nunc. Aliquam posuere, lorem porttitor feugiat ultricies, sem urna venenatis metus, vitae ornare justo leo at libero. Aliquam fringilla lectus at ullamcorper venenatis. Sed et elementum sapienm sed quis ipsum.", admin, programming);
        topicService.createTopic(topic2);

        postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 2", topic2, admin));
        postService.addPost(new Post("To jest drugi komentarz do tematu nr 2", topic2, admin));
        postService.addPost(new Post("To jest trzeci komentarz do tematu nr 2", topic2, admin));

        topicService.createTopic(new Topic("Kupno telewizora za 2000zl", "Content 1", admin, sport));
        topicService.createTopic(new Topic("Pomoc w wyborze roweru na komunię", "Content 2", admin, sport));
        topicService.createTopic(new Topic("Title 3", "Content 3", admin, sport));
        topicService.createTopic(new Topic("Title 4", "Content 4", admin, sport));
        topicService.createTopic(new Topic("Title 5", "Content 5", admin, sport));
        topicService.createTopic(new Topic("Title 6", "Content 6", admin, sport));

        Topic topic6 = new Topic("Kupno telewizora za 2000zl", "Cras vel sapien sagittis, vulputate felis sit amet, volutpat arcu. Cras euismod metus eros, ut lobortis elit egestas ut. Aliquam at posuere metus. In luctus nibh mi, non tincidunt tortor feugiat sed. Morbi non venenatis nunc. Aliquam posuere, lorem porttitor feugiat ultricies, sem urna venenatis metus, vitae ornare justo leo at libero. Aliquam fringilla lectus at ullamcorper venenatis. Sed et elementum sapienm sed quis ipsum.", admin, programming);
        topicService.createTopic(topic6);

        postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 1", topic6, admin));
        postService.addPost(new Post("To jest drugi komentarz do tematu nr 1", topic6, admin));
        postService.addPost(new Post("To jest trzeci komentarz do tematu nr 1", topic6, admin));

        Topic topic7 = new Topic("Kupno telewizora za 5000zl", "Cras vel sapien sagittis, vulputate felis sit amet, volutpat arcu. Cras euismod metus eros, ut lobortis elit egestas ut. Aliquam at posuere metus. In luctus nibh mi, non tincidunt tortor feugiat sed. Morbi non venenatis nunc. Aliquam posuere, lorem porttitor feugiat ultricies, sem urna venenatis metus, vitae ornare justo leo at libero. Aliquam fringilla lectus at ullamcorper venenatis. Sed et elementum sapienm sed quis ipsum.", admin, programming);
        topicService.createTopic(topic7);

        postService.addPost(new Post("To jest pierwszy komentarz do tematu nr 2", topic7, admin));
        postService.addPost(new Post("To jest drugi komentarz do tematu nr 2", topic7, admin));
        postService.addPost(new Post("To jest trzeci komentarz do tematu nr 2", topic7, admin));

        topicService.createTopic(new Topic("Kupno telewizora za 2000zl", "Content 1", admin, sport));
        topicService.createTopic(new Topic("Pomoc w wyborze roweru na komunię", "Content 2", admin, sport));
        topicService.createTopic(new Topic("Title 3", "Content 3", admin, programming));
        topicService.createTopic(new Topic("Title 4", "Content 4", admin, electronic));
        topicService.createTopic(new Topic("Title 5", "Content 5", admin, programming));
        topicService.createTopic(new Topic("Title 6", "Content 6", admin, electronic));

        System.out.println("Data successfully created");
    }
}

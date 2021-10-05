package pl.lukasz94w.myforum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.service.PostService;
import pl.lukasz94w.myforum.service.TopicService;

import javax.annotation.PostConstruct;

@Component
public class TestDataConstructor {

    private final TopicService topicService;
    private final PostService postService;

    @Autowired
    public TestDataConstructor(TopicService topicService, PostService postService) {
        this.topicService = topicService;
        this.postService = postService;
    }

    @PostConstruct
    public void createTestData() {
        System.out.println("Creating test data...");

        Topic topic1 = new Topic("Kupno telewizora za 2000zl", "Cras vel sapien sagittis, vulputate felis sit amet, volutpat arcu. Cras euismod metus eros, ut lobortis elit egestas ut. Aliquam at posuere metus. In luctus nibh mi, non tincidunt tortor feugiat sed. Morbi non venenatis nunc. Aliquam posuere, lorem porttitor feugiat ultricies, sem urna venenatis metus, vitae ornare justo leo at libero. Aliquam fringilla lectus at ullamcorper venenatis. Sed et elementum sapienm sed quis ipsum.");
        topicService.createTopic(topic1);

        postService.createPost(new Post("To jest pierwszy komentarz do posta nr 1", topic1));
        postService.createPost(new Post("To jest drugi komentarz do posta nr 1", topic1));
        postService.createPost(new Post("To jest trzeci komentarz do posta nr 1", topic1));

        Topic topic2 = new Topic("Kupno telewizora za 5000zl", "Cras vel sapien sagittis, vulputate felis sit amet, volutpat arcu. Cras euismod metus eros, ut lobortis elit egestas ut. Aliquam at posuere metus. In luctus nibh mi, non tincidunt tortor feugiat sed. Morbi non venenatis nunc. Aliquam posuere, lorem porttitor feugiat ultricies, sem urna venenatis metus, vitae ornare justo leo at libero. Aliquam fringilla lectus at ullamcorper venenatis. Sed et elementum sapienm sed quis ipsum.");
        topicService.createTopic(topic2);

        postService.createPost(new Post("To jest pierwszy komentarz do posta nr 2", topic2));
        postService.createPost(new Post("To jest drugi komentarz do posta nr 2", topic2));
        postService.createPost(new Post("To jest trzeci komentarz do posta nr 2", topic2));

        topicService.createTopic(new Topic("Kupno telewizora za 2000zl", "Content 1"));
        topicService.createTopic(new Topic("Pomoc w wyborze roweru na komunię", "Content 2"));
        topicService.createTopic(new Topic("Title 3", "Content 3"));
        topicService.createTopic(new Topic("Title 4", "Content 4"));
        topicService.createTopic(new Topic("Title 5", "Content 5"));
        topicService.createTopic(new Topic("Title 6", "Content 6"));

//        commentService.

        System.out.println("Data successfully created");
    }
}

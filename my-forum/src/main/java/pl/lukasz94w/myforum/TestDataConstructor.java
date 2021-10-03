package pl.lukasz94w.myforum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.service.PostService;

import javax.annotation.PostConstruct;

@Component
public class TestDataConstructor {

    private final PostService postService;

    @Autowired
    public TestDataConstructor(PostService postService) {
        this.postService = postService;
    }

    @PostConstruct
    public void createTestData() {
        System.out.println("Creating test data...");

        postService.createPost(new Post("Kupno telewizora za 2000zl", "Content 1"));
        postService.createPost(new Post("Pomoc w wyborze roweru na komunię", "Content 2"));
        postService.createPost(new Post("Title 3", "Content 3"));
        postService.createPost(new Post("Title 4", "Content 4"));
        postService.createPost(new Post("Title 5", "Content 5"));
        postService.createPost(new Post("Title 6", "Content 6"));

        System.out.println("Data successfully created");
    }
}

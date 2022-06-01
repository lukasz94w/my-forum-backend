package pl.lukasz94w.myforum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.NewTopicContent;
import pl.lukasz94w.myforum.security.auth.AuthorizedUserProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    AuthorizedUserProvider authorizedUserProvider;
    @Mock
    TopicRepository topicRepository;
    @Mock
    PostRepository postRepository;
    @InjectMocks
    TopicService topicService;

    @Test
    void shouldCreateTopicAndReturnItId() {
        // given
        NewTopicContent newTopicContent = new NewTopicContent("New topic", "Some content", "programming");
        String userName = "user1";
        User user = new User();
        Category programing = new Category(EnumeratedCategory.PROGRAMMING);

        given(authorizedUserProvider.getAuthorizedUserName()).willReturn(userName);
        given(userRepository.findByName(userName)).willReturn(user);
        given(categoryRepository.findByEnumeratedCategory(EnumeratedCategory.PROGRAMMING)).willReturn(programing);
        Topic savedTopic = new Topic(newTopicContent.getTitle(), user, programing);
        savedTopic.setId(1L);
        given(topicRepository.save(any())).willReturn(savedTopic);
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);

        // when
        Long idOfCreatedTopic = topicService.createTopic(newTopicContent);

        // then
        then(postRepository)
                .should(atLeastOnce())
                .save(argumentCaptor.capture());

        Post savedFirstPost = argumentCaptor.getValue();
        assertEquals(newTopicContent.getContent(), savedFirstPost.getContent());
        assertEquals(1, savedFirstPost.getNumber());
        assertEquals(savedTopic, savedFirstPost.getTopic());
        assertEquals(user, savedFirstPost.getUser());

        assertEquals(savedTopic.getId(), idOfCreatedTopic);
    }

    @Test
    void shouldThrowExceptionWhenTryingAddTopicWithCategoryThatDoesNotExist() {
        // given
        NewTopicContent newTopicContent = new NewTopicContent("New topic", "Some content", "non existing category");
        String userName = "user1";

        given(authorizedUserProvider.getAuthorizedUserName()).willReturn(userName);
        given(userRepository.findByName(userName)).willReturn(new User());

        // when, then
        ForumItemNotFoundException exception = assertThrows(ForumItemNotFoundException.class, () -> {
            topicService.createTopic(newTopicContent);
        });

        assertEquals("Such category doesn't exist", exception.getForumItemNotFoundExceptionReason().getExceptionMessage());
        then(topicRepository)
                .should(never())
                .save(any(Topic.class));
        then(postRepository)
                .should(never())
                .save(any(Post.class));
    }
}
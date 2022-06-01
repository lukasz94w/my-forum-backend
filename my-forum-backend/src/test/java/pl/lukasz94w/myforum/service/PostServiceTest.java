package pl.lukasz94w.myforum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lukasz94w.myforum.exception.exception.PostAddException;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Post;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.NewPostContent;
import pl.lukasz94w.myforum.security.auth.AuthorizedUserProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    TopicRepository topicRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    AuthorizedUserProvider authorizedUserProvider;
    @InjectMocks
    PostService postService;

    @Test
    void shouldAddPostToExistingTopic() {
        // given
        NewPostContent newPostContent = new NewPostContent("Some content", 1L);
        String someUser = "user2";
        User user = new User();
        given(authorizedUserProvider.getAuthorizedUserName()).willReturn(someUser);
        given(userRepository.findByName(someUser)).willReturn(user);
        Topic topic = new Topic("Some title", new User(), new Category(EnumeratedCategory.PROGRAMMING));
        topic.setId(1L);
        given(topicRepository.findById(1L)).willReturn(Optional.of(topic));
        int numberOfFoundPosts = 3;
        given(postRepository.countPostByTopic(topic)).willReturn(numberOfFoundPosts);
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);

        // when
        postService.addPost(newPostContent);

        // then
        then(topicRepository)
                .should(atLeastOnce())
                .save(topic);

        then(postRepository)
                .should(atLeastOnce())
                .save(argumentCaptor.capture());

        Post post = argumentCaptor.getValue();
        assertEquals(newPostContent.getContent(), post.getContent());
        assertEquals(topic, post.getTopic());
        assertEquals(numberOfFoundPosts + 1, post.getNumber());
        assertEquals(user, post.getUser());
    }

    @Test
    void shouldThrowExceptionWhenAddingPostToClosedTopic() {
        // given
        String someUser = "user3";
        given(authorizedUserProvider.getAuthorizedUserName()).willReturn(someUser);
        given(userRepository.findByName(someUser)).willReturn(new User());
        Topic topic = new Topic("Some title", new User(), new Category(EnumeratedCategory.ADVERTISEMENT));
        topic.setClosed(true);
        given(topicRepository.findById(1L)).willReturn(Optional.of(topic));

        // when, then
        PostAddException exception = assertThrows(PostAddException.class, () -> {
            postService.addPost(new NewPostContent("Some content", 1L));
        });

        assertEquals("Topic was closed. You can't write in it anymore", exception.getPostAddExceptionReason().getExceptionMessage());

        then(topicRepository)
                .should(never())
                .save(any(Topic.class));
        then(postRepository)
                .should(never())
                .save(any(Post.class));
    }
}
package pl.lukasz94w.myforum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.lukasz94w.myforum.exception.reason.PostAddExceptionReason;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.PostRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("user1")
class PostControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @Transactional
    void shouldAddPost() throws Exception {
        User admin = userRepository.findByName("user2");
        Category someCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.SPORT);
        Topic testTopic = new Topic("Test topic", admin, someCategory);
        topicRepository.save(testTopic);

        mockMvc.perform(post("/post/addPost")
                        .content("{\"content\": \"Some content\", \"topicId\": \"" + testTopic.getId() + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(201))
                .andReturn();
    }

    @Test
    void shouldNotAllowAddPostWithTooShortContentLength() throws Exception {
        mockMvc.perform(post("/post/addPost")
                        .content("{\"content\": \"\", \"topicId\": \"1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void shouldNotAllowToAddPostToNotExistingTopic() throws Exception {
        int idOfNotExistingTopic = 9876543;
        mockMvc.perform(post("/post/addPost")
                        .content("{\"content\": \"Some content\", \"topicId\": \"" + idOfNotExistingTopic + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(PostAddExceptionReason.TOPIC_DOESNT_EXIST.getExceptionMessage()))
                .andExpect(status().is(410))
                .andReturn();
    }

    @Test
    @Transactional
    void shouldNotAllowAddPostToClosedTopic() throws Exception {
        User user1 = userRepository.findByName("user1"); // could be also new User created
        Category someCategoryFromDb = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.CAR);
        Topic closedTopic = new Topic("Some title", user1, someCategoryFromDb);
        closedTopic.setClosed(true);
        topicRepository.save(closedTopic);

        mockMvc.perform(post("/post/addPost")
                        .content("{\"content\": \"Some content\", \"topicId\": \"" + closedTopic.getId() + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(PostAddExceptionReason.TOPIC_WAS_CLOSED.getExceptionMessage()))
                .andExpect(status().is(HttpStatus.LOCKED.value()))
                .andReturn();
    }
}
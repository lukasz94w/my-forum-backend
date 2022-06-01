package pl.lukasz94w.myforum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.model.Category;
import pl.lukasz94w.myforum.model.Topic;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedCategory;
import pl.lukasz94w.myforum.repository.CategoryRepository;
import pl.lukasz94w.myforum.repository.TopicRepository;
import pl.lukasz94w.myforum.repository.UserRepository;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TopicControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @Transactional
    void shouldReturnAddedTopicById() throws Exception {
        User admin = userRepository.findByName("admin");
        Category someCategory = categoryRepository.findByEnumeratedCategory(EnumeratedCategory.SPORT);
        Topic testTopic = new Topic("Test topic", admin, someCategory);
        topicRepository.save(testTopic);

        MvcResult mvcResult = mockMvc.perform(get("/topic/getTopicById/" + testTopic.getId()))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        Topic readTopic = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Topic.class);
        assertNotNull(readTopic);
        assertEquals(readTopic.getTitle(), testTopic.getTitle());
        assertFalse(readTopic.isClosed());
    }

    @Test
    @WithUserDetails("user2")
    void shouldNotAllowAddNewTopicBecauseOfWrongCategory() throws Exception {
        String categoryWhichDoesNotExist = "blablablah";

        mockMvc.perform(post("/topic/addTopic/")
                        .content("{\"title\": \"Some title\", \"content\": \"Some content\", \"category\": \"" + categoryWhichDoesNotExist + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ForumItemNotFoundExceptionReason.CATEGORY_DOESNT_EXIST.getExceptionMessage()))
                .andExpect(status().is(404));
    }

    @Test
    void shouldReturnCategoryDoesntExist() throws Exception {
        String categoryWhichDoesntExist = "blablablah";

        mockMvc.perform(get("/topic/findPageableTopicsInCategory/")
                        .param("page", "1")
                        .param("category", categoryWhichDoesntExist))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ForumItemNotFoundExceptionReason.CATEGORY_DOESNT_EXIST.getExceptionMessage()))
                .andExpect(status().is(404));
    }

    @Test
    void shouldNotAllowAddNewTopicWithoutAuthorization() throws Exception {
        mockMvc.perform(post("/topic/addTopic/")
                        .content("{\"title\": \"Some title\", \"content\": \"Some content\", \"category\": \"programming\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @Transactional
    void shouldFindAtLeastOneTopicBasedOnTitleName() throws Exception {
        int currentPage = 1;
        mockMvc.perform(get("/topic/searchInTopicTitles")
                        .param("page", String.valueOf(currentPage))
                        .param("query", ""))
                .andDo(print())
                .andExpect(jsonPath("$.totalPages", greaterThan(0)))
                .andExpect(jsonPath("$.currentPage").value(currentPage))
                .andExpect(jsonPath("$.totalTopics", greaterThan(0)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldNotFindAnyTopicBasedOnTitleName() throws Exception {
        String query = "gwg98g8h9348g43g";
        int currentPage = 1;
        mockMvc.perform(get("/topic/searchInTopicTitles")
                        .param("page", String.valueOf(currentPage))
                        .param("query", query))
                .andDo(print())
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.currentPage").value(currentPage))
                .andExpect(jsonPath("$.totalTopics").value(0))
                .andExpect(status().isOk());
    }
}
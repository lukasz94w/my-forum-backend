package pl.lukasz94w.myforum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnInfoAboutExistingUser() throws Exception {
        String existingUserName = "admin";
        mockMvc.perform(get("/user/getUserInfo/" + existingUserName))
                .andDo(print())
                .andExpect(jsonPath("$.name").value(existingUserName))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"))
                .andExpect(jsonPath("$.profilePic").doesNotExist())
                .andExpect(jsonPath("$.registered").exists())
                .andExpect(jsonPath("$.reasonOfBan").doesNotExist())
                .andExpect(jsonPath("$.banned").value("false"))
                .andExpect(jsonPath("$.admin").value("true"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUserDoesntExist() throws Exception {
        String nonExistingUserName = "hrehrehrehrehe";
        mockMvc.perform(get("/user/getUserInfo/" + nonExistingUserName))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST.getExceptionMessage()))
                .andExpect(status().is(404));
    }

    @Test
    @WithUserDetails("user5")
    void shouldNotAllowNormalUserToGetRegisteredUsersList() throws Exception {
        int pageNumber = 1;
        mockMvc.perform(get("/user/findPageableUsers/" + pageNumber))
                .andDo(print())
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("admin")
    void shouldAllowAdminToGetRegisteredUsersList() throws Exception {
        int pageNumber = 1;
        mockMvc.perform(get("/user/findPageableUsers/" + pageNumber))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
package pl.lukasz94w.myforum.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.lukasz94w.myforum.exception.reason.SignInExceptionReason;
import pl.lukasz94w.myforum.exception.reason.SignUpExceptionReason;
import pl.lukasz94w.myforum.model.Ban;
import pl.lukasz94w.myforum.model.Role;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.model.enums.EnumeratedRole;
import pl.lukasz94w.myforum.repository.BanRepository;
import pl.lukasz94w.myforum.repository.RoleRepository;
import pl.lukasz94w.myforum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BanRepository banRepository;

    @Test
    void shouldSuccessfullyLogin() throws Exception {
        mockMvc.perform(post("/auth/signIn")
                        .content("{\"username\": \"user1\", \"password\": \"user1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.accessToken").value(containsString("eyJ")))
                .andExpect(jsonPath("$.refreshToken").value(containsString("eyJ")));
    }

    @Test
    void shouldRejectLoginAttemptWithNonExistingUser() throws Exception {
        mockMvc.perform(post("/auth/signIn")
                        .content("{\"username\": \"nonExistingUserName\", \"password\": \"notExistingUserPass\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.message").value(SignInExceptionReason.BAD_CREDENTIALS.getExceptionMessage()));
    }

    @Test
    @Transactional
    void shouldAllowLoggedInUserToCreateNewTopic() throws Exception {
        MvcResult loginRequestResult = mockMvc.perform(post("/auth/signIn")
                        .content("{\"username\": \"user1\", \"password\": \"user1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();

        String accessToken = JsonPath.read(loginRequestResult.getResponse().getContentAsString(), "$.accessToken");

        MvcResult addTopicRequestResult = mockMvc.perform(post("/topic/addTopic")
                        .content("{\"title\": \"Example title\", \"content\": \"Some content\", \"category\": \"programming\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andDo(print())
                .andReturn();

        Integer createdTopicId = Integer.parseInt(addTopicRequestResult.getResponse().getContentAsString());
        assertNotNull(createdTopicId);
    }

    @Test
    @Transactional
    void shouldNotAllowRegisterUserWithAlreadyExistingName() throws Exception {
        User user = prepareUserForTest();

        mockMvc.perform(post("/auth/signUp")
                        .content("{\"username\": \"" + user.getName() + "\", " +
                                "\"email\": \"diffEmail@google.com\", " +
                                "\"password\": \"somePassword\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(SignUpExceptionReason.USERNAME_IS_TAKEN.getExceptionMessage()));
    }

    @Test
    @Transactional
    void shouldNotAllowRegisterUserWithAlreadyExistingEmail() throws Exception {
        User user = prepareUserForTest();

        mockMvc.perform(post("/auth/signUp")
                        .content("{\"username\": \"differentName\", " +
                                "\"email\": \"" + user.getEmail() + "\", " +
                                "\"password\": \"somePassword\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(SignUpExceptionReason.EMAIL_IS_TAKEN.getExceptionMessage()));
    }

    @Test
    @Transactional
    void checkIfFreshlySignedUpUserAccountNeedsEmailVerification() throws Exception {
        String userName = "userName";
        String userPassword = "userPassword";

        mockMvc.perform(post("/auth/signUp")
                        .content("{\"username\": \"" + userName + "\", " +
                                "\"email\": \"someEmail@gmail.com\", " +
                                "\"password\": \"" + userPassword + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful! Check email for confirmation link"));

        mockMvc.perform(post("/auth/signIn")
                        .content("{\"username\": \"" + userName + "\", " +
                                "\"password\": \"" + userPassword + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isTooEarly())
                .andExpect(jsonPath("$.message").value(SignInExceptionReason.ACCOUNT_NOT_ACTIVATED.getExceptionMessage()));
    }

    @Test
    @Transactional
    void shouldBannedUserReceiveBanMessage() throws Exception {
        User user = prepareUserForTest();
        banUser(user);

        mockMvc.perform(post("/auth/signIn")
                        .content("{\"username\": \"" + user.getName() + "\", " +
                                "\"password\": \"somePassword\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(423))
                .andExpect(jsonPath("$.message").value(SignInExceptionReason.USER_IS_BANNED.getExceptionMessage()));
    }

    // test user could be also created in @BeforeAll
    User prepareUserForTest() {
        String userName = "someUser";
        String userEmail = "someUser@gmail.com";
        String userPassword = "$2a$10$XQg0ZVZduzENUaM3sOcW0eKMpE7ptG5PHlo288vX2EBf6I/0Qfbam"; // "somePassword" generated using https://www.javainuse.com/onlineBcrypt
        Role roleUser = roleRepository.findByEnumeratedRole(EnumeratedRole.ROLE_USER);
        Set<Role> roles = new HashSet<>(List.of(roleUser));
        User user = new User(userName, userEmail, userPassword, roles);
        userRepository.save(user);
        return user;
    }

    private void banUser(User user) {
        Ban userBan = new Ban(user, "For flooding", LocalDateTime.now().plusWeeks(1).withHour(23).withMinute(59).withSecond(59));
        user.setBan(userBan);
        banRepository.save(userBan);
    }
}
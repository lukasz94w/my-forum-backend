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
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BanControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Transactional
    @WithUserDetails("admin")
    void shouldBanExistingUser() throws Exception {
        String tomorrowDateInIsoFormat = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);
        String userName = "user1";

        mockMvc.perform(put("/ban/banUser/")
                        .content("{\"dateOfBan\": \"" + tomorrowDateInIsoFormat + "\", \"reasonOfBan\": \"Swearing\", \"userName\": \"" + userName + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().string("")); // body should be empty
//                .andExpect(jsonPath("$").doesNotExist()) // other way of checking if body is empty

        mockMvc.perform(get("/ban/checkBanStatus/" + userName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithUserDetails("admin")
    void shouldNotBeAbleToBanUserWithBanDateFromThePast() throws Exception {
        String pastWeekDateInIsoFormat = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ISO_DATE);

        mockMvc.perform(put("/ban/banUser/")
                        .content("{\"dateOfBan\": \"" + pastWeekDateInIsoFormat + "\", \"reasonOfBan\": \"Swearing\", \"userName\": \"user1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    void shouldNotBeAbleToBanUserWithoutAuthorization() throws Exception {
        String someDateFromTheFuture = LocalDate.now().plusDays(15).format(DateTimeFormatter.ISO_DATE);

        mockMvc.perform(put("/ban/banUser/")
                        .content("{\"dateOfBan\": \"" + someDateFromTheFuture + "\", \"reasonOfBan\": \"Swearing\", \"userName\": \"user1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails("user1")
    void shouldNotBeAbleToBanUserByNormalUser() throws Exception {
        String someDateFromTheFuture = LocalDate.now().plusDays(15).format(DateTimeFormatter.ISO_DATE);

        mockMvc.perform(put("/ban/banUser/")
                        .content("{\"dateOfBan\": \"" + someDateFromTheFuture + "\", \"reasonOfBan\": \"Swearing\", \"userName\": \"user1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(403));
    }

    @Test
    @WithUserDetails("admin")
    void shouldReturnErrorWhenBanUserWhichDoesNotExist() throws Exception {
        String someDateFromTheFuture = LocalDate.now().plusDays(15).format(DateTimeFormatter.ISO_DATE);
        String userNameWhichSurelyNotExist = "fsafa9w8ur4";

        mockMvc.perform(put("/ban/banUser/")
                        .content("{\"dateOfBan\": \"" + someDateFromTheFuture + "\", \"reasonOfBan\": \"Swearing\", \"userName\": \"" + userNameWhichSurelyNotExist + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST.getExceptionMessage()));
    }
}
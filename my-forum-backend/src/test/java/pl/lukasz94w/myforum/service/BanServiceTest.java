package pl.lukasz94w.myforum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.model.Ban;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.BanRepository;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.BanRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BanServiceTest {

    @Mock
    BanRepository banRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    BanService banService;

    @Test
    void shouldBanExistingUser() {
        // given
        BanRequest banRequest = new BanRequest(LocalDate.now().plusDays(2), "For swearing", "user1");
        User user = new User();
        user.setName("user1");
        given(userRepository.findUserByName("user1")).willReturn(java.util.Optional.of(user));
        given(banRepository.findByUser(user)).willReturn(null); // no ban has been given to this user before
        ArgumentCaptor<Ban> argumentCaptor = ArgumentCaptor.forClass(Ban.class);

        // when
        banService.banUser(banRequest);

        // then
        then(banRepository)
                .should(atLeastOnce())
                .save(argumentCaptor.capture());
        Ban savedBan = argumentCaptor.getValue();
        assertEquals(banRequest.getDateOfBan().atTime(23, 59, 59), savedBan.getDateAndTimeOfBan());
        assertEquals(banRequest.getReasonOfBan(), savedBan.getReasonOfBan());
        assertEquals(banRequest.getUserName(), savedBan.getUser().getName());
    }

    @Test
    void shouldThrowExceptionWhenTryingToBanUserWhichDoesNotExist() {
        // given
        BanRequest banRequest = new BanRequest(LocalDate.now().plusDays(2), "For swearing", "user1");
        given(userRepository.findUserByName("user1")).willThrow(new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST));

        // when
        ForumItemNotFoundException exception = assertThrows(ForumItemNotFoundException.class, () -> {
            banService.banUser(banRequest);
        });

        // then
        assertEquals("User not found", exception.getForumItemNotFoundExceptionReason().getExceptionMessage());
        then(banRepository)
                .should(never())
                .save(any(Ban.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToUnBanUserWhichDoesNotExist() {
        // given
        given(userRepository.findUserByName("user1")).willThrow(new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST));

        // when, then
        ForumItemNotFoundException exception = assertThrows(ForumItemNotFoundException.class, () -> {
            banService.unBanUser("user1");
        });

        assertEquals("User not found", exception.getForumItemNotFoundExceptionReason().getExceptionMessage());
        then(banRepository)
                .should(never())
                .save(any(Ban.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCheckBanUserInfoWhichDoesNotExist() {
        // given
        given(userRepository.findUserByName("user1")).willThrow(new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST));

        // when
        ForumItemNotFoundException exception = assertThrows(ForumItemNotFoundException.class, () -> {
            banService.checkBanStatus("user1");
        });

        // then
        assertEquals("User not found", exception.getForumItemNotFoundExceptionReason().getExceptionMessage());
        then(banRepository)
                .should(never())
                .save(any(Ban.class));
    }
}
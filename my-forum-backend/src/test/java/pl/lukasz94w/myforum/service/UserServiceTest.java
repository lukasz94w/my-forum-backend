package pl.lukasz94w.myforum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.lukasz94w.myforum.exception.exception.ChangePasswordViaUserSettingsException;
import pl.lukasz94w.myforum.exception.exception.ForumItemNotFoundException;
import pl.lukasz94w.myforum.exception.reason.ForumItemNotFoundExceptionReason;
import pl.lukasz94w.myforum.model.User;
import pl.lukasz94w.myforum.repository.UserRepository;
import pl.lukasz94w.myforum.request.ChangePasswordViaUserSettings;
import pl.lukasz94w.myforum.response.dto.mapper.MapperDto;
import pl.lukasz94w.myforum.response.message.SuccessResponse;
import pl.lukasz94w.myforum.security.auth.AuthorizedUserProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    MapperDto mapperDto;
    @Mock
    AuthorizedUserProvider authorizedUserProvider;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserService userService;

    @Test
    void shouldThrowExceptionWhenTryingToGetInfoAboutUserWhichDoesNotExist() {
        // given
        String userNameWhichDoesNotExist = "user";
        given(userRepository.findUserByName(userNameWhichDoesNotExist)).willThrow(new ForumItemNotFoundException(ForumItemNotFoundExceptionReason.USER_DOESNT_EXIST));

        // when, then
        ForumItemNotFoundException exception = assertThrows(ForumItemNotFoundException.class, () -> {
            userService.getUserInfo(userNameWhichDoesNotExist);
        });

        assertEquals("User not found", exception.getForumItemNotFoundExceptionReason().getExceptionMessage());
        then(mapperDto)
                .should(never())
                .mapToUserDto2(any(User.class));
    }

    @Test
    void shouldChangePasswordWhenCurrentOneInRequestIsCorrect() {
        // given
        User user = new User();
        String userName = "user";
        user.setName(userName);
        String existingHashedPassword = "exi5tingHa$hedP4ssw0rd";
        user.setPassword(existingHashedPassword);

        ChangePasswordViaUserSettings changePasswordViaUserSettings = new ChangePasswordViaUserSettings("currentPassword", "newPassword");

        given(authorizedUserProvider.getAuthorizedUserName()).willReturn(userName);
        given(userRepository.findUserByName(userName)).willReturn(java.util.Optional.of(user));
        given(passwordEncoder.matches(changePasswordViaUserSettings.getCurrentPassword(), existingHashedPassword)).willReturn(true);
        given(passwordEncoder.encode(changePasswordViaUserSettings.getNewPassword())).willReturn("newH4sh3dP4ssw0rd");

        // when
        SuccessResponse successResponse = userService.changePasswordThroughUserSettings(changePasswordViaUserSettings);

        // then
        then(userRepository)
                .should(atLeastOnce())
                .save(user);
        assertEquals("Password changed successfully", successResponse.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordInRequestIsInvalid() {
        // given
        User user = new User();
        String userName = "user";
        user.setName(userName);
        String existingHashedPassword = "exi5tingH4shedPa55w0rd";
        user.setPassword(existingHashedPassword);

        ChangePasswordViaUserSettings changePasswordViaUserSettings = new ChangePasswordViaUserSettings("invalidCurrentPassword", "newPassword");

        given(authorizedUserProvider.getAuthorizedUserName()).willReturn(userName);
        given(userRepository.findUserByName(userName)).willReturn(java.util.Optional.of(user));
        given(passwordEncoder.matches(changePasswordViaUserSettings.getCurrentPassword(), existingHashedPassword))
                .willThrow(new ChangePasswordViaUserSettingsException("Current password is not correct"));

        // when, then
        ChangePasswordViaUserSettingsException exception = assertThrows(ChangePasswordViaUserSettingsException.class, () -> {
            userService.changePasswordThroughUserSettings(changePasswordViaUserSettings);
        });

        assertEquals("Current password is not correct", exception.getMessage());
        then(userRepository)
                .should(never())
                .save(any(User.class));
    }
}
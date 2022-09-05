package pl.lukasz94w.myforum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.lukasz94w.myforum.exception.reason.*;
import pl.lukasz94w.myforum.exception.exception.*;
import pl.lukasz94w.myforum.response.message.BanResponse;
import pl.lukasz94w.myforum.response.message.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooMuchSizeOfProfilePicException() {
        return new ResponseEntity<>(new ErrorResponse("File too large. Maximum size is 0.2MB"), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(ProfilePicUploadException.class)
    public ResponseEntity<ErrorResponse> handleIOExceptionDuringProfilePicUpload(ProfilePicUploadException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ChangePasswordViaUserSettingsException.class)
    public ResponseEntity<ErrorResponse> handleExceptionWhileChangingPassword(ChangePasswordViaUserSettingsException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PostAddException.class)
    public ResponseEntity<ErrorResponse> handleExceptionsWhileAddingPost(PostAddException exception) {
        PostAddExceptionReason reason = exception.getPostAddExceptionReason();
        return new ResponseEntity<>(new ErrorResponse(reason.getExceptionMessage()), reason.getHttpStatus());
    }

    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<ErrorResponse> handleSignUpExceptions(SignUpException exception) {
        SignUpExceptionReason reason = exception.getSignUpExceptionReason();
        return new ResponseEntity<>(new ErrorResponse(reason.getExceptionMessage()), reason.getHttpStatus());
    }

    @ExceptionHandler(SignInException.class)
    public ResponseEntity<ErrorResponse> handleSignInExceptions(SignInException exception) {
        SignInExceptionReason reason = exception.getSignInExceptionReason();
        if (reason.equals(SignInExceptionReason.USER_IS_BANNED)) {
            return new ResponseEntity<>(new BanResponse(reason.getExceptionMessage(), exception.getBannedUserData()), reason.getHttpStatus());
        } else {
            return new ResponseEntity<>(new ErrorResponse(reason.getExceptionMessage()), reason.getHttpStatus());
        }
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(RefreshTokenException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ChangePasswordViaEmailLinkException.class)
    public ResponseEntity<ErrorResponse> handleExceptionsWhenChangePasswordViaEmailLink(ChangePasswordViaEmailLinkException exception) {
        ChangePasswordViaEmailLinkExceptionReason reason = exception.getChangePasswordViaEmailLinkExceptionReason();
        return new ResponseEntity<>(new ErrorResponse(reason.getExceptionMessage()), reason.getHttpStatus());
    }

    @ExceptionHandler(ActivateAccountException.class)
    public ResponseEntity<ErrorResponse> handleActivateAccountExceptions(ActivateAccountException exception) {
        AccountActivationExceptionReason reason = exception.getAccountActivationExceptionReason();
        return new ResponseEntity<>(new ErrorResponse(reason.getExceptionMessage()), reason.getHttpStatus());
    }

    @ExceptionHandler(ResendActivationTokenException.class)
    public ResponseEntity<ErrorResponse> handleResendActivationTokenException(ResendActivationTokenException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ForumItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleForumItemNotFoundException(ForumItemNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getForumItemNotFoundExceptionReason().getExceptionMessage()), HttpStatus.NOT_FOUND);
    }
}

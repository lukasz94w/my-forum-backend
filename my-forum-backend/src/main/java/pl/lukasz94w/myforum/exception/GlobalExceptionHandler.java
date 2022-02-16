package pl.lukasz94w.myforum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.lukasz94w.myforum.exception.enums.*;
import pl.lukasz94w.myforum.exception.exception.*;
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
        PostAddExceptionEnum exceptionEnum = exception.getPostAddExceptionEnum();
        return new ResponseEntity<>(new ErrorResponse(exceptionEnum.getExceptionMessage()), exceptionEnum.getHttpStatus());
    }

    @ExceptionHandler(SignUpException.class)
    public ResponseEntity<ErrorResponse> handleSignUpExceptions(SignUpException exception) {
        SignUpExceptionEnum exceptionEnum = exception.getSignUpExceptionEnum();
        return new ResponseEntity<>(new ErrorResponse(exceptionEnum.getExceptionMessage()), exceptionEnum.getHttpStatus());
    }

    @ExceptionHandler(SignInException.class)
    public ResponseEntity<ErrorResponse> handleSignInExceptions(SignInException exception) {
        SignInExceptionEnum exceptionEnum = exception.getSignInExceptionEnum();
        if (exceptionEnum.equals(SignInExceptionEnum.USER_IS_BANNED)) {
            return new ResponseEntity<>(new ErrorResponse(exceptionEnum.getExceptionMessage(), exception.getBannedUserData()), exceptionEnum.getHttpStatus());
        } else {
            return new ResponseEntity<>(new ErrorResponse(exceptionEnum.getExceptionMessage()), exceptionEnum.getHttpStatus());
        }
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(RefreshTokenException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ChangePasswordViaEmailLinkException.class)
    public ResponseEntity<ErrorResponse> handleExceptionsWhenChangePasswordViaEmailLink(ChangePasswordViaEmailLinkException exception) {
        ChangePasswordViaEmailLinkExceptionEnum exceptionEnum = exception.getChangePasswordViaEmailLinkExceptionEnum();
        return new ResponseEntity<>(new ErrorResponse(exceptionEnum.getExceptionMessage()), exceptionEnum.getHttpStatus());
    }

    @ExceptionHandler(ActivateAccountException.class)
    public ResponseEntity<ErrorResponse> handleActivateAccountExceptions(ActivateAccountException exception) {
        ActivateAccountExceptionEnum exceptionEnum = exception.getActivateAccountExceptionEnum();
        return new ResponseEntity<>(new ErrorResponse(exceptionEnum.getExceptionMessage()), exceptionEnum.getHttpStatus());
    }

    @ExceptionHandler(ResendActivationTokenException.class)
    public ResponseEntity<ErrorResponse> handleResendActivationTokenException(ResendActivationTokenException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> defaultExceptionHandler(Exception exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

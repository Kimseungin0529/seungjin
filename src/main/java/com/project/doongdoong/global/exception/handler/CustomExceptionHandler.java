package com.project.doongdoong.global.exception.handler;

import com.project.doongdoong.global.common.ErrorResponse;
import com.project.doongdoong.global.exception.CustomException;
import com.project.doongdoong.global.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.doongdoong.global.exception.ErrorType.BadRequest.BAD_REQUEST_DEFAULT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    // Valid Exception
    @ResponseStatus(value = BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<String> errors = ex.getAllValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ErrorResponse(new CustomException(BAD_REQUEST_DEFAULT, "잘못된 입력 형식이 존재합니다."), errors);
    }


    // Dto Valid Exception
    @ResponseStatus(value = BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ErrorResponse handleDtoNotValid(MethodArgumentNotValidException ex) {

        // MethodArgumentNotValidException에서 FieldError들을 가져와서 메시지를 추출하여 리스트에 추가
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream( )
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ErrorResponse(new CustomException(BAD_REQUEST_DEFAULT, "잘못된 입력 형식이 존재합니다."), errors);
    }

    /*
   Security Exception
    */
    @ResponseStatus(value = BAD_REQUEST)
    @ExceptionHandler(value = CustomException.InvalidRequestException.class)
    public ErrorResponse handleBadRequest(CustomException ex) {
        return new ErrorResponse(ex, null);
        //return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(ex, null));
    }
    @ResponseStatus(value = UNAUTHORIZED)
    @ExceptionHandler(CustomException.UnauthorizedException.class)
    public ErrorResponse handleUnauthorized(CustomException ex) {
        return new ErrorResponse(ex, null);
        //return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponse(ex, null));
    }
    @ResponseStatus(value = FORBIDDEN)
    @ExceptionHandler(CustomException.ForbiddenException.class)
    public ErrorResponse handleForbidden(CustomException ex) {
        return new ErrorResponse(ex, null);
        //return ResponseEntity.status(FORBIDDEN).body(new ErrorResponse(ex, null));
    }
    @ResponseStatus(value = NOT_FOUND)
    @ExceptionHandler(CustomException.NotFoundException.class)
    public ErrorResponse handleNotfound(CustomException ex) {
        return new ErrorResponse(ex, null);
        //return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(ex, null));
    }
    @ResponseStatus(value = CONFLICT)
    @ExceptionHandler(CustomException.ConflictException.class)
    public ErrorResponse handleConflict(CustomException ex) {
        return new ErrorResponse(ex, null);
        //return ResponseEntity.status(CONFLICT).body(new ErrorResponse(ex, null));
    }

    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomException.ServerErrorException.class)
    public ErrorResponse handleServerError(CustomException ex) {
        return new ErrorResponse(ex, null);
        //return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex, null));
    }

    /**
     * 커스텀 예외가 아닌 예외 발생 시(커스텀에서 제외된 RuntimeException)에 대한 예외 처리도 필요
     */
}

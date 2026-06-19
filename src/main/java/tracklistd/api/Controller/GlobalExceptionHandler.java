package tracklistd.api.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tracklistd.api.Dto.ErrorDto;
import tracklistd.api.Exceptions.CommentExceptions.CommentOnCommentException;
import tracklistd.api.Exceptions.CommentExceptions.CommentOwershipViolation;
import tracklistd.api.Exceptions.CommentExceptions.CommentTextBlankException;
import tracklistd.api.Exceptions.CommentExceptions.SelfCommentException;
import tracklistd.api.Exceptions.MediaListExceptions.InvalidMediaTypeForListException;
import tracklistd.api.Exceptions.MediaListExceptions.ListNameBlankException;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListNameAlreadyExitsException;
import tracklistd.api.Exceptions.MediaListExceptions.MediaListaOwnershipViolation;
import tracklistd.api.Exceptions.RatingsExceptions.InvalidRatingNote;
import tracklistd.api.Exceptions.RatingsExceptions.RatingAlreadyExists;
import tracklistd.api.Exceptions.RatingsExceptions.RatingOwnershipViolation;
import tracklistd.api.Exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //Captura as exceções de Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> argumentNotValidException(MethodArgumentNotValidException ex)
    {
        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        //Montar o Dto de Erro
        ErrorDto errorDto = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.name())
                .errors(errorList)
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    //Captura exceções personalizadas que retornam um erro do tipo 400
    @ExceptionHandler({
            InvalidRatingNote.class,
            ListNameBlankException.class,
            CommentTextBlankException.class
    })
    public ResponseEntity<ErrorDto> badRequestException(RuntimeException ex)
    {
        ErrorDto errorDto = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.name())
                .errors(List.of(ex.getMessage()))
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    //Captura exceções personalizadas que retornam um erro do tipo 403
    @ExceptionHandler({
            CommentOwershipViolation.class,
            MediaListaOwnershipViolation.class,
            RatingOwnershipViolation.class,
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<ErrorDto> forbiddenException(RuntimeException ex)
    {
        ErrorDto errorDto = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.FORBIDDEN.value())
                .status(HttpStatus.FORBIDDEN.name())
                .errors(List.of(ex.getMessage()))
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    //Captura exceções personalizadas que retornam um erro do tipo 404
    @ExceptionHandler({
            ResourceNotFoundException.class
    })
    public ResponseEntity<ErrorDto> notFoundException(RuntimeException ex)
    {
        ErrorDto errorDto = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND.name())
                .errors(List.of(ex.getMessage()))
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
    //Captura exceções personalizadas que retornam um erro do tipo 409
    @ExceptionHandler({
            MediaListNameAlreadyExitsException.class,
            RatingAlreadyExists.class
    })
    public ResponseEntity<ErrorDto> conflictException(RuntimeException ex)
    {
        ErrorDto errorDto = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.CONFLICT.value())
                .status(HttpStatus.CONFLICT.name())
                .errors(List.of(ex.getMessage()))
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    //Captura exceções personalizadas que retornam um erro do tipo 422
    @ExceptionHandler({
            CommentOnCommentException.class,
            SelfCommentException.class,
            InvalidMediaTypeForListException.class
    })
    public ResponseEntity<ErrorDto> unprocessableContentException(RuntimeException ex)
    {
        ErrorDto errorDto = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.UNPROCESSABLE_CONTENT.value())
                .status(HttpStatus.UNPROCESSABLE_CONTENT.name())
                .errors(List.of(ex.getMessage()))
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    //Para erro do Swagger
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        // Retorna 404 sem corpo, ou com uma mensagem amigável de "Recurso não encontrado"
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso não encontrado.");
    }

    //Captura quaisquer exceções não esperada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> genericException(Exception ex) {

        logger.error("Erro interno inesperado. Tente novamente mais tarde.", ex);

        ErrorDto apiError = ErrorDto
                .builder()
                .timestamp(LocalDateTime.now())
                .codeError(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errors(List.of("Erro interno inesperado. Tente novamente mais tarde."))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

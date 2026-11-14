package online.store.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> defaultErrorHandler(
            HttpServletRequest request,
            Exception exception) throws Exception {

        System.out.println(
            String.format(
                "Exception in handling request to %s: %s",
                request.getRequestURI(),
                exception.getMessage()
            )
        );

        exception.printStackTrace();

        return new ResponseEntity<>(
            exception.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
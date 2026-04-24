package com.library.fine.interfaces.rest;

import com.library.fine.domain.exception.FineAlreadyPaidException;
import com.library.fine.domain.exception.FineNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FineNotFoundException.class)
    public ProblemDetail handleFineNotFound(FineNotFoundException ex) {
        log.warn("Fine not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Fine Not Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(FineAlreadyPaidException.class)
    public ProblemDetail handleFineAlreadyPaid(FineAlreadyPaidException ex) {
        log.warn("Fine already paid: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Fine Already Paid");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid Request");
        problem.setDetail(ex.getMessage());
        return problem;
    }
}

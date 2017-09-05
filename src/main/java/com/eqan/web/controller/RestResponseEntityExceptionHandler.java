package com.eqan.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.eqan.web.exceptions.NotAuthorizedException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);
    private static String LOG_EXCEPTION_TEMPLATE = "{} occurreed: {}";

    @ExceptionHandler(value = { IllegalArgumentException.class, NullPointerException.class })
    protected ResponseEntity<Object> handleBadParams(RuntimeException ex, WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);

    }

    @ExceptionHandler(value = { EmptyResultDataAccessException.class, DataIntegrityViolationException.class,
            DuplicateKeyException.class })
    protected ResponseEntity<Object> handleDAOExceptions(RuntimeException ex, WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(value = { NotAuthorizedException.class })
    protected ResponseEntity<Object> handleNotAuthorized(NotAuthorizedException ex, WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), new HttpHeaders(), HttpStatus.UNAUTHORIZED,
                request);

    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        logException(ex);
        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (ex.getMessage().contains("Authorization")) {
            return handleNotAuthorized(new NotAuthorizedException(), request);
        }
        logException(ex);
        return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), headers, status, request);
    }

    private void logException(Exception ex) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(LOG_EXCEPTION_TEMPLATE, ex.getClass().getSimpleName(), ex.getMessage());
        }
        if (LOG.isTraceEnabled())
            ex.printStackTrace();
    }

    private Map<String, String> getErrorMap(String error) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", error);
        return errorMap;
    }

    @PostConstruct
    private void postConstruct() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} constructed", this.getClass().getSimpleName());
        }
    }
}

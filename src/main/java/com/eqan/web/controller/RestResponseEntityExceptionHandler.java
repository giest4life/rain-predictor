package com.eqan.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{
	private static Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);
	private static String LOG_EXCEPTION_TEMPLATE = "{} occurreed: {}";
	
	@ExceptionHandler(value = { IllegalArgumentException.class, NullPointerException.class })
	protected ResponseEntity<Object> handleBadParams(RuntimeException ex, WebRequest request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_EXCEPTION_TEMPLATE, ex.getClass().getSimpleName(), ex.getMessage());
		}
		return handleExceptionInternal(ex, getErrorMap(ex.getMessage()),
				new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

	}
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(LOG_EXCEPTION_TEMPLATE, ex.getClass().getSimpleName(), ex.getMessage());
		}
		return handleExceptionInternal(ex, getErrorMap(ex.getMessage()), headers, status, request);
	}
	
	private Map<String, String> getErrorMap(String error) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("error", error);
		return errorMap;
	}
	
	@PostConstruct
	private void postConstruct() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} constructed", this.getClass().getSimpleName());
		}
	}
}

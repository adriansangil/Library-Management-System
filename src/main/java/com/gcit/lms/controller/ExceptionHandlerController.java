package com.gcit.lms.controller;

import java.sql.SQLException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.gcit.lms.error.ErrorResponse;

@ControllerAdvice
public class ExceptionHandlerController {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
		ex.printStackTrace();
		ErrorResponse error = new ErrorResponse();
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		error.setMessage(ex.getMessage());
		error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		if(ex instanceof EmptyResultDataAccessException) {
			error.setErrorCode(HttpStatus.NOT_FOUND.value());
			error.setMessage(ex.getMessage());
			httpStatus = HttpStatus.NOT_FOUND;
		}
		
		if(ex instanceof MethodArgumentTypeMismatchException|| ex instanceof DataIntegrityViolationException) {
			error.setErrorCode(HttpStatus.BAD_REQUEST.value());
			error.setMessage("Bad request");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		
		if(ex instanceof SQLException) {
			error.setErrorCode(HttpStatus.BAD_REQUEST.value());
			error.setMessage("You may have tried adding something that does not exist");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		
		if(ex instanceof NoHandlerFoundException) {
			error.setErrorCode(HttpStatus.NOT_FOUND.value());
			error.setMessage(ex.getMessage());
			httpStatus = HttpStatus.NOT_FOUND;
		}
		if(ex instanceof HttpMessageNotReadableException) {
			error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			error.setMessage("Request Body error");
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<ErrorResponse>(error, httpStatus);
	}

}

package com.digi.sign.controller.advice.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.digi.sign.controller.advice.exception.dto.ErrorTO;
import com.digi.sign.exception.DigiSignException;
import com.digi.sign.exception.DigiSignUserException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

@RestControllerAdvice
public class DigiSignControllerAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(DigiSignControllerAdvice.class);

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ErrorTO> handleException(Throwable ex) {
		LOGGER.error("\n{}", ExceptionUtils.getStackTrace(ex));

		HttpStatus errorCode;
		List<String> errorMsgs = new ArrayList<>();

		HttpStatus httpStatusForException = getHttpStatusForException(ex);
		if (httpStatusForException != null) {
			errorCode = httpStatusForException;
			errorMsgs.add(ex.getMessage());
		} else {
			errorCode = HttpStatus.INTERNAL_SERVER_ERROR;
			errorMsgs.add("Unknown error occured");
		}

		return createErrorTo(errorCode, errorMsgs);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorTO> handleBindExceptionException(BindException ex) {
		LOGGER.error("\n{}", ExceptionUtils.getStackTrace(ex));

		List<String> errorMsgs = ex.getAllErrors().stream()
				.map(error -> ((FieldError) error).getField() + " " + error.getDefaultMessage())
				.collect(Collectors.toList());
		return createErrorTo(HttpStatus.BAD_REQUEST, errorMsgs);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorTO> handleInvalidTypeIdException(HttpMessageNotReadableException ex) {
		LOGGER.error("\n{}", ExceptionUtils.getStackTrace(ex));

		List<String> errorMsgs = new ArrayList<>();
		if (ex.getCause() instanceof InvalidTypeIdException) {
			InvalidTypeIdException cause = (InvalidTypeIdException) ex.getCause();
			String errorMessage = cause.getTypeId() != null
					? cause.getTypeId() + " is not a valid integrator document type"
					: "integratorName missing";
			errorMsgs.add(errorMessage);
			return createErrorTo(HttpStatus.BAD_REQUEST, errorMsgs);
		}

		if (ex.getCause() instanceof JsonProcessingException) {
			JsonMappingException cause = (JsonMappingException) ex.getCause();
			errorMsgs.add(cause.getOriginalMessage());
			return createErrorTo(HttpStatus.BAD_REQUEST, errorMsgs);
		}

		errorMsgs.add(ex.getMessage());
		return createErrorTo(HttpStatus.BAD_REQUEST, errorMsgs);
	}

	private ResponseEntity<ErrorTO> createErrorTo(HttpStatus errorCode, List<String> errorMsgs) {
		ErrorTO error = new ErrorTO();
		error.setErrorCode(errorCode.toString());
		error.setErrorMsgs(errorMsgs);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(error, headers, errorCode);
	}

	private HttpStatus getHttpStatusForException(Throwable ex) {
		if (ex instanceof HttpRequestMethodNotSupportedException) {
			return HttpStatus.METHOD_NOT_ALLOWED;
		} else if (ex instanceof HttpMediaTypeNotSupportedException) {
			return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
		} else if (ex instanceof HttpMediaTypeNotAcceptableException) {
			return HttpStatus.NOT_ACCEPTABLE;
		} else if (ex instanceof MissingPathVariableException) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		} else if (ex instanceof MissingServletRequestParameterException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof ServletRequestBindingException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof ConversionNotSupportedException) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		} else if (ex instanceof TypeMismatchException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof HttpMessageNotReadableException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof HttpMessageNotWritableException) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		} else if (ex instanceof MethodArgumentNotValidException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof MissingServletRequestPartException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof BindException) {
			return HttpStatus.BAD_REQUEST;
		} else if (ex instanceof NoHandlerFoundException) {
			return HttpStatus.NOT_FOUND;
		} else if (ex instanceof AsyncRequestTimeoutException) {
			return HttpStatus.SERVICE_UNAVAILABLE;
		} else if (ex instanceof DigiSignException) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		} else if (ex instanceof DigiSignUserException) {
			return HttpStatus.FORBIDDEN;
		} else {
			return null;
		}
	}
}

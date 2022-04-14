package com.digi.sign.controller.advice.exception.dto;

import java.util.List;

public class ErrorTO {

	private String errorCode;

	private List<String> errorMsgs;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public List<String> getErrorMsgs() {
		return errorMsgs;
	}

	public void setErrorMsgs(List<String> errorMsgs) {
		this.errorMsgs = errorMsgs;
	}
}

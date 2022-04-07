package com.digi.sign.dto.esp.request;

public class ESPRequestTO {

	private String espRequest;

	private String aspTxnId;

	private String contentType;
	
	private String espUrl;

	public String getEspRequest() {
		return espRequest;
	}

	public void setEspRequest(String espRequest) {
		this.espRequest = espRequest;
	}

	public String getAspTxnId() {
		return aspTxnId;
	}

	public void setAspTxnId(String aspTxnId) {
		this.aspTxnId = aspTxnId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEspUrl() {
		return espUrl;
	}

	public void setEspUrl(String espUrl) {
		this.espUrl = espUrl;
	}
}

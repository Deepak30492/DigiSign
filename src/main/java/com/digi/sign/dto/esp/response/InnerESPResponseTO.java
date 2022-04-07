package com.digi.sign.dto.esp.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class InnerESPResponseTO {

	@JacksonXmlProperty(isAttribute = true)
	private String status;

	@JacksonXmlProperty(localName = "resCode", isAttribute = true)
	private String responseCode;

	@JacksonXmlProperty(localName = "errCode", isAttribute = true)
	private String errorCode;

	@JacksonXmlProperty(localName = "errMsg", isAttribute = true)
	private String errorMessage;

	@JacksonXmlProperty(localName = "ts", isAttribute = true)
	private String timestamp;

	@JacksonXmlProperty(localName = "txn", isAttribute = true)
	private String txnId;

	@JacksonXmlProperty(localName = "UserX509Certificate")
	private String userX509Certificate;

	@JacksonXmlProperty(localName = "Signatures")
	private SignatureTO pdfSignature;

	@JacksonXmlProperty(localName = "Signature")
	private Object espSignature;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getUserX509Certificate() {
		return userX509Certificate;
	}

	public void setUserX509Certificate(String userX509Certificate) {
		this.userX509Certificate = userX509Certificate;
	}

	public SignatureTO getPdfSignature() {
		return pdfSignature;
	}

	public void setPdfSignature(SignatureTO pdfSignature) {
		this.pdfSignature = pdfSignature;
	}

	public Object getEspSignature() {
		return espSignature;
	}

	public void setEspSignature(Object espSignature) {
		this.espSignature = espSignature;
	}
}

package com.digi.sign.dto.esp.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Esign")
public class ESPTO {

	@JacksonXmlProperty(localName = "ver", isAttribute = true)
	private String version;

	@JacksonXmlProperty(localName = "sc", isAttribute = true)
	private String signerConsent;

	@JacksonXmlProperty(localName = "ts", isAttribute = true)
	private String timestamp;

	@JacksonXmlProperty(localName = "txn", isAttribute = true)
	private String txnId;

	@JacksonXmlProperty(localName = "ekycIdType", isAttribute = true)
	private String eKycIdType;

	@JacksonXmlProperty(localName = "ekycId", isAttribute = true)
	private String eKycId;

	@JacksonXmlProperty(isAttribute = true)
	private String aspId;

	@JacksonXmlProperty(localName = "AuthMode", isAttribute = true)
	private String authMode;

	@JacksonXmlProperty(localName = "responseSigType", isAttribute = true)
	private String responseSignType;

	@JacksonXmlProperty(localName = "responseUrl", isAttribute = true)
	private String responseUrl;

	@JacksonXmlProperty(localName = "Docs")
	private DocumentTO doc;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSignerConsent() {
		return signerConsent;
	}

	public void setSignerConsent(String signerConsent) {
		this.signerConsent = signerConsent;
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

	public String getEKycIdType() {
		return eKycIdType;
	}

	public void setEKycIdType(String eKycIdType) {
		this.eKycIdType = eKycIdType;
	}

	public String getEKycId() {
		return eKycId;
	}

	public void setEKycId(String eKycId) {
		this.eKycId = eKycId;
	}

	public String getAspId() {
		return aspId;
	}

	public void setAspId(String aspId) {
		this.aspId = aspId;
	}

	public String getAuthMode() {
		return authMode;
	}

	public void setAuthMode(String authMode) {
		this.authMode = authMode;
	}

	public String getResponseSignType() {
		return responseSignType;
	}

	public void setResponseSignType(String responseSignType) {
		this.responseSignType = responseSignType;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}

	public DocumentTO getDoc() {
		return doc;
	}

	public void setDoc(DocumentTO doc) {
		this.doc = doc;
	}
}

package com.digi.sign.dto.esp.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ESPResponseFormTO {

	@JacksonXmlProperty(localName = "eSignResponse")
	private String espOuterResponse;

	@JacksonXmlProperty(localName = "espTxnID")
	private String espTxnId;

	public String getEspOuterResponse() {
		return espOuterResponse;
	}

	public void setEspOuterResponse(String espOuterResponse) {
		this.espOuterResponse = espOuterResponse;
	}

	public String getEspTxnId() {
		return espTxnId;
	}

	public void setEspTxnId(String espTxnId) {
		this.espTxnId = espTxnId;
	}
}

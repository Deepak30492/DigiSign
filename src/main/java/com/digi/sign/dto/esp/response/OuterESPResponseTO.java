package com.digi.sign.dto.esp.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "eSignResponse")
public class OuterESPResponseTO {

	@JacksonXmlProperty(localName = "EsignResp")
	private String espResponse;

	public String getEspResponse() {
		return espResponse;
	}

	public void setEspResponse(String espResponse) {
		this.espResponse = espResponse;
	}
}

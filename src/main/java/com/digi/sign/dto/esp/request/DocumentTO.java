package com.digi.sign.dto.esp.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class DocumentTO {

	@JacksonXmlProperty(localName = "InputHash")
	private InputHashTO inputHash;

	public InputHashTO getInputHash() {
		return inputHash;
	}

	public void setInputHash(InputHashTO inputHash) {
		this.inputHash = inputHash;
	}
}

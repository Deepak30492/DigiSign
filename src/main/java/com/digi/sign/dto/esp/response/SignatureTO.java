package com.digi.sign.dto.esp.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SignatureTO {

	@JacksonXmlProperty(localName = "DocSignature")
	private DocSignatureTO docSign;

	public DocSignatureTO getDocSign() {
		return docSign;
	}

	public void setDocSign(DocSignatureTO docSign) {
		this.docSign = docSign;
	}
}

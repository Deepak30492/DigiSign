package com.digi.sign.dto.esp.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class DocSignatureTO {

	@JacksonXmlProperty(isAttribute = true)
	private String id;

	@JacksonXmlProperty(localName = "sigHashAlgorithm", isAttribute = true)
	private String signHashAlgorithm;

	@JacksonXmlProperty(isAttribute = true)
	private String error;

	@JacksonXmlText
	private String pkcs7CmsContainer;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSignHashAlgorithm() {
		return signHashAlgorithm;
	}

	public void setSignHashAlgorithm(String signHashAlgorithm) {
		this.signHashAlgorithm = signHashAlgorithm;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getPkcs7CmsContainer() {
		return pkcs7CmsContainer;
	}

	public void setPkcs7CmsContainer(String pkcs7CmsContainer) {
		this.pkcs7CmsContainer = pkcs7CmsContainer;
	}
}

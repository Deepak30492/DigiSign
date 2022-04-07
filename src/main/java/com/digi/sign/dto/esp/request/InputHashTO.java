package com.digi.sign.dto.esp.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class InputHashTO {

	@JacksonXmlProperty(isAttribute = true)
	private String id;

	@JacksonXmlProperty(isAttribute = true)
	private String hashAlgorithm;

	@JacksonXmlProperty(isAttribute = true)
	private String docInfo;

	@JacksonXmlText
	private String pdfHash;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public String getDocInfo() {
		return docInfo;
	}

	public void setDocInfo(String docInfo) {
		this.docInfo = docInfo;
	}

	public String getPdfHash() {
		return pdfHash;
	}

	public void setPdfHash(String pdfHash) {
		this.pdfHash = pdfHash;
	}
}

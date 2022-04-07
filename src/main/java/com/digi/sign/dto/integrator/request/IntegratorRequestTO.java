package com.digi.sign.dto.integrator.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class IntegratorRequestTO {

	@NotNull
	@Valid
	private IntegratorDocumentTO document;

	public IntegratorDocumentTO getDocument() {
		return document;
	}

	public void setDocument(IntegratorDocumentTO document) {
		this.document = document;
	}
}

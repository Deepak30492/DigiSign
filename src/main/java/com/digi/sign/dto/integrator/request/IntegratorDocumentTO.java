package com.digi.sign.dto.integrator.request;

import com.digi.sign.dto.integrator.hfr.request.HFRDocumentTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "integratorName", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = HFRDocumentTO.class, name = "HFR") })
public abstract class IntegratorDocumentTO {

	private String integratorName;

	public String getIntegratorName() {
		return integratorName;
	}

	public void setIntegratorName(String integratorName) {
		this.integratorName = integratorName;
	}
}

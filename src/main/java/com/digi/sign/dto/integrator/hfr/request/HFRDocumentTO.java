package com.digi.sign.dto.integrator.hfr.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.digi.sign.constant.HFRDocTemplateEnum;
import com.digi.sign.dto.integrator.request.IntegratorDocumentTO;
import com.digi.sign.exception.handling.EnumValue;

public class HFRDocumentTO extends IntegratorDocumentTO {

	@NotNull
	@EnumValue(type = HFRDocTemplateEnum.class)
	private String templateId;

	@NotBlank
	private String submitterName;

	@NotBlank
	private String signerName;

	@NotBlank
	private String hpId;

	@NotBlank
	private String mobileNumber;

	@NotBlank
	private String emailId;

	@NotBlank
	private String signingPlace;

	@NotEmpty
	@Valid
	List<FacilityTO> facilities;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getSubmitterName() {
		return submitterName;
	}

	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}

	public String getSignerName() {
		return signerName;
	}

	public void setSignerName(String signerName) {
		this.signerName = signerName;
	}

	public String getHpId() {
		return hpId;
	}

	public void setHpId(String hpId) {
		this.hpId = hpId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getSigningPlace() {
		return signingPlace;
	}

	public void setSigningPlace(String signingPlace) {
		this.signingPlace = signingPlace;
	}

	public List<FacilityTO> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<FacilityTO> facilities) {
		this.facilities = facilities;
	}
}

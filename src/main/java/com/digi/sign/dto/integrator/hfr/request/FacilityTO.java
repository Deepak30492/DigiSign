package com.digi.sign.dto.integrator.hfr.request;

import javax.validation.constraints.NotBlank;

public class FacilityTO {

	@NotBlank
	private String srNo;

	@NotBlank
	private String facilityId;

	@NotBlank
	private String facilityName;

	@NotBlank
	private String stateOrUt;

	@NotBlank
	private String district;

	@NotBlank
	private String addressWithPincode;

	@NotBlank
	private String facilityOwnership;

	@NotBlank
	private String status;

	@NotBlank
	private String submittedDate;

	@NotBlank
	public String getSrNo() {
		return srNo;
	}

	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getStateOrUt() {
		return stateOrUt;
	}

	public void setStateOrUt(String stateOrUt) {
		this.stateOrUt = stateOrUt;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddressWithPincode() {
		return addressWithPincode;
	}

	public void setAddressWithPincode(String addressWithPincode) {
		this.addressWithPincode = addressWithPincode;
	}

	public String getFacilityOwnership() {
		return facilityOwnership;
	}

	public void setFacilityOwnership(String facilityOwnership) {
		this.facilityOwnership = facilityOwnership;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}
}

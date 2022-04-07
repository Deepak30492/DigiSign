package com.digi.sign.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.digi.sign.dto.integrator.hfr.request.FacilityTO;
import com.digi.sign.dto.integrator.hfr.request.HFRDocumentTO;
import com.digi.sign.dto.integrator.request.IntegratorRequestTO;
import com.digi.sign.exception.DigiSignException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DigiSignController.class)
class DigiSignControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void generatePdfAndSignTest() throws Exception {
		mockMvc.perform(post("/digiSign/signDoc") //
				.contentType(MediaType.APPLICATION_JSON) //
				.content(createSuccessJson()));
	}

	public String createSuccessJson() throws DigiSignException, JsonProcessingException {
		HFRDocumentTO document = new HFRDocumentTO();
		document.setTemplateId("TEMPLATE_1");
		document.setSubmitterName("John Nadar");
		document.setSignerName("John Nadar");
		document.setHpId("HP#568792");
		document.setMobileNumber("7730498299");
		document.setEmailId("john@gmail.com");
		document.setSigningPlace("Pune");
		document.setIntegratorName("HFR");

		FacilityTO facility1 = new FacilityTO();
		facility1.setSrNo("1");
		facility1.setFacilityId("Faci#89203");
		facility1.setFacilityName("Sun Hospital");
		facility1.setStateOrUt("Maharashtra");
		facility1.setDistrict("Thane");
		facility1.setAddressWithPincode("102, xyz nagar, Thane");
		facility1.setFacilityOwnership("Sunil Sheth");
		facility1.setStatus("Operational");
		facility1.setSubmittedDate("06-04-2022");

		FacilityTO facility2 = new FacilityTO();
		facility2.setSrNo("2");
		facility2.setFacilityId("Faci#89204");
		facility2.setFacilityName("Moon Hospital");
		facility2.setStateOrUt("Maharashtra");
		facility2.setDistrict("Thane");
		facility2.setAddressWithPincode("102, bcde nagar, Thane");
		facility2.setFacilityOwnership("Sunil Sheth");
		facility2.setStatus("Operational");
		facility2.setSubmittedDate("06-04-2022");

		FacilityTO facility3 = new FacilityTO();
		facility3.setSrNo("3");
		facility3.setFacilityId("Faci#89205");
		facility3.setFacilityName("Star Hospital");
		facility3.setStateOrUt("Maharashtra");
		facility3.setDistrict("Thane");
		facility3.setAddressWithPincode("102, fghd nagar, Thane");
		facility3.setFacilityOwnership("Sunil Sheth");
		facility3.setStatus("Operational");
		facility3.setSubmittedDate("06-04-2022");

		FacilityTO facility4 = new FacilityTO();
		facility4.setSrNo("4");
		facility4.setFacilityId("Faci#89206");
		facility4.setFacilityName("Galaxy Hospital");
		facility4.setStateOrUt("Maharashtra");
		facility4.setDistrict("Thane");
		facility4.setAddressWithPincode("102, rtoe nagar, Thane");
		facility4.setFacilityOwnership("Sunil Sheth");
		facility4.setStatus("Operational");
		facility4.setSubmittedDate("06-04-2022");
		document.setFacilities(new ArrayList<>(Arrays.asList(facility1, facility2, facility3, facility4)));

		IntegratorRequestTO request = new IntegratorRequestTO();
		request.setDocument(document);

		return objectMapper.writeValueAsString(request);
	}
}

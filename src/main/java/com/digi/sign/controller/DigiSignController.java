package com.digi.sign.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digi.sign.dto.esp.request.ESPRequestTO;
import com.digi.sign.dto.integrator.request.IntegratorRequestTO;
import com.digi.sign.exception.DigiSignException;
import com.digi.sign.exception.DigiSignUserException;
import com.digi.sign.service.DigiSignService;

@RestController
public class DigiSignController {

	@Autowired
	private DigiSignService digiSignService;

	@PostMapping(path = "/genEspRequest", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ESPRequestTO> generatePdfAndSign(@RequestBody @Valid IntegratorRequestTO request)
			throws DigiSignException {
		ESPRequestTO espRequest = digiSignService.generateEspRequest(request);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(espRequest, headers, HttpStatus.OK);
	}

	@PostMapping(path = "/api/espResponse", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<FileSystemResource> handleEspResponse(@RequestParam Map<String, String> responseMap)
			throws DigiSignException, DigiSignUserException {
		FileSystemResource pdf = digiSignService.handleEspResponse(responseMap);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdf.getFilename());
		return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
	}
}

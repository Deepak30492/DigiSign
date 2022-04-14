package com.digi.sign.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.digi.sign.configuration.ESPProperties;
import com.digi.sign.dto.esp.request.DocumentTO;
import com.digi.sign.dto.esp.request.ESPRequestTO;
import com.digi.sign.dto.esp.request.ESPTO;
import com.digi.sign.dto.esp.request.InputHashTO;
import com.digi.sign.dto.esp.response.ESPResponseFormTO;
import com.digi.sign.dto.esp.response.InnerESPResponseTO;
import com.digi.sign.dto.integrator.request.IntegratorRequestTO;
import com.digi.sign.exception.DigiSignException;
import com.digi.sign.exception.DigiSignUserException;
import com.digi.sign.service.DigiSignService;
import com.digi.sign.service.PDFService;
import com.digi.sign.service.XMLService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DigiSignServiceImpl implements DigiSignService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DigiSignServiceImpl.class);

	private static final List<String> USER_FORWARDABLE_CODES = new ArrayList<>(
			Arrays.asList("110", "111", "950", "952", "953"));

	@Autowired
	private ESPProperties espProperties;

	@Autowired
	private PDFService pdfService;

	@Autowired
	private XMLService xMLService;

	@Autowired
	private ObjectMapper xmlMapper;

	@Override
	public ESPRequestTO generateEspRequest(IntegratorRequestTO request) throws DigiSignException {
		ESPTO esp = createEspTo(request);

		String espXml = xMLService.getEspXml(esp);

		String signedEspXml = xMLService.getSignedEspXml(espXml);
		LOGGER.info("Request XML :\n{}", signedEspXml);

		ESPRequestTO espRequest = new ESPRequestTO();
		espRequest.setAspTxnId(esp.getTxnId());
		espRequest.setEspRequest(signedEspXml);
		espRequest.setContentType(MediaType.APPLICATION_XML_VALUE);
		espRequest.setEspUrl(espProperties.getUrl());

		return espRequest;
	}

	private ESPTO createEspTo(IntegratorRequestTO request) throws DigiSignException {
		ESPTO esp = new ESPTO();
		esp.setVersion(espProperties.getVersion());
		esp.setSignerConsent("Y");
		esp.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
		esp.setTxnId(UUID.randomUUID().toString().replace("-", ""));
		esp.setEKycIdType("A");
		esp.setEKycId("");
		esp.setAspId(espProperties.getAspId());
		esp.setAuthMode("1");
		esp.setResponseSignType("pkcs7");
		esp.setResponseUrl(espProperties.getAspUrl() + "/digiSign/api/espResponse");

		InputHashTO inputHash = new InputHashTO();
		inputHash.setId("1");
		inputHash.setHashAlgorithm("SHA256");
		pdfService.populateInputHash(inputHash, request.getDocument(),
				LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(10).toString(), esp.getTxnId());

		DocumentTO doc = new DocumentTO();
		doc.setInputHash(inputHash);
		esp.setDoc(doc);

		return esp;
	}

	@Override
	public FileSystemResource handleEspResponse(Map<String, String> requestParams)
			throws DigiSignException, DigiSignUserException {
		ESPResponseFormTO espResponseForm = xmlMapper.convertValue(requestParams,
				new TypeReference<ESPResponseFormTO>() {
				});
		try {
			InnerESPResponseTO espResponse = xmlMapper.readValue(espResponseForm.getEspOuterResponse().getBytes(),
					InnerESPResponseTO.class);
			if (!"1".equals(espResponse.getStatus())) {
				if (USER_FORWARDABLE_CODES.contains(espResponse.getErrorCode())) {
					LOGGER.error(espResponseForm.getEspOuterResponse());
					throw new DigiSignUserException(espResponse.getErrorCode() + " : " + espResponse.getErrorMessage());
				} else {
					LOGGER.error(espResponseForm.getEspOuterResponse());
					throw new DigiSignException(
							"Internal error occurred, received error from ESP! Please try again or contact DigiSign");
				}
			}
			return pdfService.signPdf(espResponse.getPdfSignature().getDocSign().getPkcs7CmsContainer(),
					espResponse.getTxnId());

		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException(
					"Internal error occurred during ESP response parsing! Please try again or contact DigiSign");

		}

	}
}

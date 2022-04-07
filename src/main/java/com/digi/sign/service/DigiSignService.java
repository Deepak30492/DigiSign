package com.digi.sign.service;

import java.util.Map;

import org.springframework.core.io.FileSystemResource;

import com.digi.sign.dto.esp.request.ESPRequestTO;
import com.digi.sign.dto.integrator.request.IntegratorRequestTO;
import com.digi.sign.exception.DigiSignException;
import com.digi.sign.exception.DigiSignUserException;

public interface DigiSignService {

	ESPRequestTO generateEspRequest(IntegratorRequestTO request) throws DigiSignException;

	FileSystemResource handleEspResponse(Map<String, String> requestParams) throws DigiSignException, DigiSignUserException;
}

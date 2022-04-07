package com.digi.sign.service;

import org.springframework.core.io.FileSystemResource;

import com.digi.sign.dto.esp.request.InputHashTO;
import com.digi.sign.dto.integrator.request.IntegratorDocumentTO;
import com.digi.sign.exception.DigiSignException;

public interface PDFService {

	void populateInputHash(InputHashTO inputHash, IntegratorDocumentTO document, String dateWithTime, String pdfName)
			throws DigiSignException;

	FileSystemResource signPdf(String pkcs7CmsContainer, String pdfName) throws DigiSignException;
}

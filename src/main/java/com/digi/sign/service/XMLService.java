package com.digi.sign.service;

import com.digi.sign.dto.esp.request.ESPTO;
import com.digi.sign.exception.DigiSignException;

public interface XMLService {

	String getEspXml(ESPTO esp) throws DigiSignException;

	String getSignedEspXml(String esp) throws DigiSignException;

}

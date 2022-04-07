package com.digi.sign.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;

import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.digi.sign.configuration.ESPProperties;
import com.digi.sign.dto.esp.request.ESPTO;
import com.digi.sign.exception.DigiSignException;
import com.digi.sign.service.XMLService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class XMLServiceImpl implements XMLService {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLServiceImpl.class);

	@Autowired
	private ObjectMapper xmlMapper;

	@Autowired
	private ESPProperties espProperties;

	@Override
	public String getEspXml(ESPTO esp) throws DigiSignException {
		try {
			return xmlMapper.writeValueAsString(esp);
		} catch (JsonProcessingException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException("Internal error occurred during xml creation! Please try again or contact DigiSign");
		}
	}

	@Override
	public String getSignedEspXml(String espXml) throws DigiSignException {
		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null),
					Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
					null, null);

			SignedInfo si = fac.newSignedInfo(
					fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
							(C14NMethodParameterSpec) null),
					fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null), Collections.singletonList(ref));

			XMLSignature signature = fac.newXMLSignature(si, null);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			// or completely disable external entities declarations:
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			// or prohibit the use of all protocols by external entities:
			dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			dbf.setNamespaceAware(true);

			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(espXml.getBytes(StandardCharsets.UTF_8)));
			DOMSignContext dsc = new DOMSignContext(getPrivateKeyFromKeyStore(), doc.getDocumentElement());

			signature.sign(dsc);

			TransformerFactory transFact = TransformerFactory.newInstance();
			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transFact.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

			Transformer transFormer = transFact.newTransformer();
			StringWriter sw = new StringWriter();
			transFormer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (ParserConfigurationException | SAXException | IOException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException | MarshalException | XMLSignatureException
				| TransformerException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException("Internal error occurred during xml signing! Please try again or contact DigiSign");
		}
	}

	private PrivateKey getPrivateKeyFromKeyStore() throws DigiSignException {
		try {
			KeyStore ks = KeyStore.getInstance(espProperties.getKeyStoreType());
			Resource keystoreFile = new DefaultResourceLoader()
					.getResource("classpath:".concat(espProperties.getKeyStorePath()));
			ks.load(keystoreFile.getInputStream(), espProperties.getKeyStorePass().toCharArray());
			return (PrivateKey) ks.getKey(espProperties.getPrivateKeyAlias(),
					espProperties.getPrivateKeyPass().toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException
				| KeyStoreException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException("Internal error occurred during private key retrieval! Please try again or contact DigiSign");
		}
	}
}

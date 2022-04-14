package com.digi.sign.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.digi.sign.constant.DigiSignGlobalConstants;
import com.digi.sign.constant.HFRDocTemplateEnum;
import com.digi.sign.constant.IntegratorEnum;
import com.digi.sign.dto.esp.request.InputHashTO;
import com.digi.sign.dto.integrator.hfr.request.FacilityTO;
import com.digi.sign.dto.integrator.hfr.request.HFRDocumentTO;
import com.digi.sign.dto.integrator.request.IntegratorDocumentTO;
import com.digi.sign.exception.DigiSignException;
import com.digi.sign.service.PDFService;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

@Service
public class PDFServiceImpl implements PDFService {

	private static final String WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL = "_with_placeholder_sign_field.pdf";

	private static final String SIGNATURE_FIELD_NAME = "digiSign1";

	private static final String SIGNED_PDF_PATH = "src/main/resources/pdfs/signed/";

	private static final Logger LOGGER = LoggerFactory.getLogger(PDFServiceImpl.class);

	public void populateInputHash(InputHashTO inputHash, IntegratorDocumentTO document, String dateWithTime,
			String pdfName) throws DigiSignException {
		if (IntegratorEnum.HFR.name().equals(document.getIntegratorName())) {
			HFRDocumentTO hfrDocument = (HFRDocumentTO) document;
			inputHash.setDocInfo(pdfName);

			if (HFRDocTemplateEnum.TEMPLATE_1.name().equals(hfrDocument.getTemplateId())) {
				createPdfInHfrTemplate1Format(pdfName, hfrDocument, dateWithTime);
			}
			inputHash.setPdfHash(getHashToSign(pdfName, hfrDocument));
		}
	}

	private void createPdfInHfrTemplate1Format(String pdfName, HFRDocumentTO hfrDocument, String dateWithTime)
			throws DigiSignException {
		try {
			try (PdfWriter pdfWriter = new PdfWriter(DigiSignGlobalConstants.UNSIGNED_PDF_PATH + pdfName + ".pdf");
					PdfDocument pdfDocument = new PdfDocument(pdfWriter);
					Document document = new Document(pdfDocument);) {

				pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, new PageBorderEventHandler());

				document.add(createConcernTable());

				document.add(createSubmissionTable());

				document.add(createIntialFormTable(hfrDocument));

				document.add(new Paragraph("\n"));

				document.add(createHospiTable(hfrDocument.getFacilities()));

				document.add(new Paragraph("\n"));

				document.add(createAppliTable());

				document.add(new Paragraph("\n"));

				document.add(createFinalFormTable(hfrDocument, dateWithTime));
			}
		} catch (IOException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException(
					"Internal error occurred during pdf creation! Please try again or contact DigiSign");
		}
	}

	private static class PageBorderEventHandler implements IEventHandler {
		@Override
		public void handleEvent(Event currentEvent) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
			PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
			Rectangle defaultPageSize = docEvent.getPage().getPageSize();
			canvas.rectangle(15, 15, defaultPageSize.getWidth() - 30, defaultPageSize.getHeight() - 30).stroke();
		}
	}

	private Table createFinalFormTable(HFRDocumentTO hfrDocument, String dateWithTime) {
		Table endTable = new Table(UnitValue.createPercentArray(new float[] { 21f, 79f })).useAllAvailableWidth();
		endTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
		endTable.setKeepTogether(true);

		Cell endCell = new Cell().add(new Paragraph("• Name : "));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(12);
		endTable.addCell(endCell);

		endCell = new Cell().add(new Paragraph(hfrDocument.getSignerName()));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(11);
		endTable.addCell(endCell);

		endCell = new Cell().add(new Paragraph("• Place : "));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(12);
		endTable.addCell(endCell);

		endCell = new Cell().add(new Paragraph(hfrDocument.getSigningPlace()));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(11);
		endTable.addCell(endCell);

		endCell = new Cell().add(new Paragraph("• Date and Time : "));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(12);
		endTable.addCell(endCell);

		endCell = new Cell().add(new Paragraph(dateWithTime));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(11);
		endTable.addCell(endCell);

		endCell = new Cell().add(new Paragraph("• Digital Signature : "));
		endCell.setBorder(Border.NO_BORDER);
		endCell.setFontSize(12);
		endTable.addCell(endCell);

		endTable.addCell(createSignatureFieldCell());
		return endTable;
	}

	private Cell createSignatureFieldCell() {
		Cell cell = new Cell();
		cell.setHeight(50);
		cell.setNextRenderer(new SignatureFieldCellRenderer(cell, SIGNATURE_FIELD_NAME));
		return cell;
	}

	private class SignatureFieldCellRenderer extends CellRenderer {
		private String name;

		public SignatureFieldCellRenderer(Cell modelElement) {
			super(modelElement);
		}

		public SignatureFieldCellRenderer(Cell modelElement, String name) {
			super(modelElement);
			this.name = name;
		}

		@Override
		public void draw(DrawContext drawContext) {
			super.draw(drawContext);
			PdfFormField field = PdfFormField.createSignature(drawContext.getDocument(), getOccupiedAreaBBox());
			field.setFieldName(name);
			field.getWidgets().get(0).setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT);
			field.getWidgets().get(0).setFlags(PdfAnnotation.PRINT);
			PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(field);
		}

		@Override
		public IRenderer getNextRenderer() {
			return new SignatureFieldCellRenderer((Cell) getModelElement());
		}
	}

	private Table createAppliTable() {
		Paragraph paragraph = new Paragraph(
				"I am the applicant of the above facility/facilities and do hereby verify that the details as submitted on the portal pertaining to the above facility/facilities are true to my personal knowledge and nothing material has been concealed or falsely stated. I request you to kindly verify that the health facility as stated actually exists and give approval to that effect so that the facility can be 'validated for existence' on the portal.\r\n"
						+ "\r\n"
						+ "I am aware that the Facility ID and related information can be used and shared with the entities working in the National Digital Health Ecosystem (NDHE) which inter alia includes stakeholders and entities such as healthcare professionals (e.g. doctors), facilities (e.g. hospitals, laboratories) and data fiduciaries (e.g. health programmes), which are registered with or linked to the Ayushman Bharat Digital Mission (ABDM), and various processes there under. I reserve the right to revoke the given consent at any point of time, subject to applicable laws, rules and regulations.\r\n"
						+ "I also acknowledge that I have read and agree to follow the \"Guidelines for Health Information Providers, Health Repository Providers, Health Information Users and Health Lockers\" as published on Ayushman Bharat Digital Mission website (Link: https://abdm.gov.in/documents/hip_hiu_policy) and National Digital Health Mission Information Security Policy 2020 (Part 2-External Ecosystem) for the linkage of the health facilities to ABDM compliant software solutions. I understand that any breach or lapse of the same may lead to revocation of access and other consequent action.\r\n"
						+ "");

		paragraph.setPadding(5);
		paragraph.setFontSize(11);

		Table applicatTable = new Table(1).useAllAvailableWidth();
		applicatTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		applicatTable.setPadding(15);

		Cell appliCell = new Cell().add(paragraph);
		applicatTable.addCell(appliCell);
		return applicatTable;
	}

	private Table createIntialFormTable(HFRDocumentTO hfrDocument) {
		Table intialFormTable = new Table(UnitValue.createPercentArray(new float[] { 37f, 63f }))
				.useAllAvailableWidth();

		Cell iniCell = new Cell().add(new Paragraph("• Name : "));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph(hfrDocument.getSubmitterName()));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph("• Healthcare Professional ID Number : "));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph(hfrDocument.getHpId()));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph("• Mobile Number : "));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph(hfrDocument.getMobileNumber()));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph("• Email ID : "));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		iniCell = new Cell().add(new Paragraph(hfrDocument.getEmailId()));
		iniCell.setBorder(Border.NO_BORDER);
		iniCell.setFontSize(11);
		intialFormTable.addCell(iniCell);

		return intialFormTable;
	}

	private Table createHospiTable(List<FacilityTO> facilities) {
		Table hospiTable = new Table(9).useAllAvailableWidth();
		hospiTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

		Cell hospiCellheader = new Cell().add(new Paragraph("SrNo"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("Facility Id"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("Facility Name"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("State/UT"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("District"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("Address & Pincode"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("Facility OwnerShip"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("Status"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		hospiCellheader = new Cell().add(new Paragraph("Submitted Date"));
		hospiCellheader.setFontSize(9);
		hospiCellheader.setTextAlignment(TextAlignment.CENTER);
		hospiTable.addCell(hospiCellheader);

		for (FacilityTO facility : facilities) {
			Cell hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getSrNo()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getFacilityId()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getFacilityName()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getStateOrUt()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getDistrict()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getAddressWithPincode()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getFacilityOwnership()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getStatus()));
			hospiTable.addCell(hospiCell);

			hospiCell = new Cell();
			hospiCell.setFontSize(8);
			hospiCell.add(new Paragraph(facility.getSubmittedDate()));
			hospiTable.addCell(hospiCell);
		}
		return hospiTable;
	}

	private Paragraph createSubmissionTable() {
		Paragraph followingPara = new Paragraph(
				"The following health facilities are submitted in Health Facility Registry of Ayushman Bharat Digital Mission by :-");
		followingPara.setFontSize(12);
		return followingPara;
	}

	private Table createConcernTable() throws IOException {
		Table paraTable = new Table(1);
		paraTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		paraTable.setFontSize(15);
		paraTable.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));

		Cell paracell = new Cell().add(new Paragraph("To Whom It May Concern"));
		paracell.setBorder(Border.NO_BORDER);
		paracell.setTextAlignment(TextAlignment.CENTER);

		paraTable.addCell(paracell);
		return paraTable;
	}

	private String getHashToSign(String pdfName, HFRDocumentTO hfrDocument) throws DigiSignException {
		try (PdfReader pdfReader = new PdfReader(DigiSignGlobalConstants.UNSIGNED_PDF_PATH + pdfName + ".pdf");
				FileOutputStream pdfWithSigField = new FileOutputStream(
						DigiSignGlobalConstants.UNSIGNED_PDF_PATH + pdfName + WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL)) {
			PdfSigner signer = new PdfSigner(pdfReader, pdfWithSigField, new StampingProperties().useAppendMode());
			signer.setFieldName(SIGNATURE_FIELD_NAME);
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.MINUTE, 10);
			signer.setSignDate(instance);
			signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

			PdfSignatureAppearance signatureAppearance = signer.getSignatureAppearance();
			signatureAppearance.setReason("Testing");
			signatureAppearance.setLocation(hfrDocument.getSigningPlace());

			PreSignatureContainer external = new PreSignatureContainer(PdfName.Adobe_PPKLite,
					PdfName.Adbe_pkcs7_detached);
			signer.signExternalContainer(external, 15000);

			Files.deleteIfExists(Paths.get(DigiSignGlobalConstants.UNSIGNED_PDF_PATH + pdfName + ".pdf"));

			return DigestUtils.sha256Hex(external.getHash());
		} catch (IOException | GeneralSecurityException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException(
					"Internal error occurred during pdf hashing! Please try again or contact DigiSign");
		}
	}

	private class PreSignatureContainer implements IExternalSignatureContainer {

		private PdfDictionary sigDic;
		private byte[] hash;

		public PreSignatureContainer(PdfName filter, PdfName subFilter) {

			sigDic = new PdfDictionary();
			sigDic.put(PdfName.Filter, filter);
			sigDic.put(PdfName.SubFilter, subFilter);
		}

		@Override
		public byte[] sign(InputStream data) {
			try {
				this.hash = data.readAllBytes();
			} catch (IOException e) {
				LOGGER.error(ExceptionUtils.getStackTrace(e));
			}
			return new byte[0];
		}

		@Override
		public void modifySigningDictionary(PdfDictionary signDic) {
			signDic.putAll(sigDic);

		}

		public byte[] getHash() {
			return hash;
		}
	}

	@Override
	public FileSystemResource signPdf(String pkcs7CmsContainer, String pdfName) throws DigiSignException {
		try (PdfReader pdfReader = new PdfReader(
				DigiSignGlobalConstants.UNSIGNED_PDF_PATH + pdfName + WITH_PLACEHOLDER_SIGN_FIELD_PDF_LABEL);
				FileOutputStream signedPdfStream = new FileOutputStream(SIGNED_PDF_PATH + pdfName + ".pdf")) {
			IExternalSignatureContainer container = new PostSignatureContainer(PdfName.Adobe_PPKLite,
					PdfName.Adbe_pkcs7_detached, pkcs7CmsContainer);
			PdfSigner.signDeferred(new PdfDocument(pdfReader), SIGNATURE_FIELD_NAME, signedPdfStream, container);
			return new FileSystemResource(SIGNED_PDF_PATH + pdfName + ".pdf");
		} catch (IOException | GeneralSecurityException e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new DigiSignException(
					"Internal error occurred during pdf signing! Please try again or contact DigiSign");
		}
	}

	private class PostSignatureContainer implements IExternalSignatureContainer {

		private PdfDictionary sigDic;
		private String pkcs7CmsContainer;

		public PostSignatureContainer(PdfName filter, PdfName subFilter, String pkcs7CmsContainer) {
			sigDic = new PdfDictionary();
			sigDic.put(PdfName.Filter, filter);
			sigDic.put(PdfName.SubFilter, subFilter);
			this.pkcs7CmsContainer = pkcs7CmsContainer;
		}

		@Override
		public byte[] sign(InputStream data) throws GeneralSecurityException {
			return Base64.decode(this.pkcs7CmsContainer);
		}

		@Override
		public void modifySigningDictionary(PdfDictionary signDic) {
			signDic.putAll(sigDic);

		}

	}
}

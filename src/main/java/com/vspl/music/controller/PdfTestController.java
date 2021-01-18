package com.vspl.music.controller;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.vspl.music.model.eo.EOGenerateSlip;
import com.vspl.music.model.eo.EOObject;
import com.vspl.music.model.eo.EOPdf;
import com.vspl.music.model.eo.EOStudentInvoiceDetail;
import com.vspl.music.model.eo.EOStudentInvoiceEditable;
import com.vspl.music.model.eo.EOStudentInvoiceMain;
import com.vspl.music.services.DBServices;
import com.vspl.music.util.DateUtil;
import com.vspl.music.util.JSONUtil;


@Path("/ajaxPdf")
public class PdfTestController {
	
	public static Logger logger = LoggerFactory.getLogger(PdfTestController.class);

	@POST
	@Path("/createInvoicePdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createPdf(HashMap<String, Object> map)
			throws DocumentException,IOException,Exception {
		
		String month = (String) map.get("month");
		//String dueDate = (String) map.get("dueDate");
		String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");
		String year = (String) map.get("year");
		String eoStudentUser = (String) map.get("eoStudentUser");
		String studentfullname = (String) map.get("studFullName");
		String status = (String) map.get("status");
		String stats = "";
		String qr = "SELECT key , value FROM eopdf_json";
		List<HashMap<String, Object>> pdfDetails = DBServices.getNativeQueryResult(qr);
		
		HashMap<String, Object> pdfContents = new HashMap<>();
		for(HashMap<String, Object> obj : pdfDetails){
			pdfContents.put((String) obj.get("key"),obj.get("value"));
		}
		
		/*if(status.equalsIgnoreCase("Cancelled")){
			stats = "C";
		}else{
			stats = "A";
		}*/
		String displayName = "EOStudentInvoiceMain_"+month+"_"+year+"_"+eoStudentUser;
		String pdfUrl = studentfullname+"_"+month+"_"+year+"_"+eoStudentUser+"_"+stats;
		String seperator = System.getProperty("file.separator");
		String pdfStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"+ seperator + "SMA";
		String pdfStorePathUrl = pdfStorePath +seperator+ pdfUrl;
		
		List<Object> pdfObject = DBServices.get("FROM EOPdf WHERE displayName = '"+displayName+"' AND isActive = true");
		String eoPdfPrimaryKey = ""; 
		if(pdfObject != null && pdfObject.size() > 0){
			EOPdf eoPdf = (EOPdf) pdfObject.get(0);
			eoPdfPrimaryKey = eoPdf.primaryKey+"";
		}
		
		//query for getting data by date range
		
		String strQuery = "SELECT d.day, d.date, CONCAT(d.start_time, '-' ,d.end_time) as time,ld.duration," + 
				" c.fees, d.slot_pk, c.course_name,  c.primary_key as coursepk FROM eodefined_slot d" + 
				" LEFT JOIN eobatch b ON eobatch_primary_key = b.primary_key" + 
				" LEFT JOIN eocourses c ON b.eocourses_primary_key = c.primary_key" + 
				" LEFT JOIN lkclass_duration ld ON c.lkclassduration_primary_key = ld.primary_key" + 
				" WHERE eostudentuser_primary_key = "+eoStudentUser+" AND month = '"+month+"' AND year = '"+year+"' ORDER by date DESC";
		
		//System.out.println("strQuery::"+strQuery);
		
		List<HashMap< String, Object>> invoiceData = DBServices.getNativeQueryResult(strQuery);
		
		TreeMap<String, Object> respMap = new TreeMap<String, Object>(); 
		for(HashMap<String, Object> invoiceObj : invoiceData) {
			HashMap<String, Object> mainMap  = (HashMap<String, Object>) respMap.get(invoiceObj.get("date"));
			
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("date", invoiceObj.get("date"));
			innerMap.put("time", invoiceObj.get("time"));
			innerMap.put("duration", invoiceObj.get("duration"));
			innerMap.put("fees", invoiceObj.get("fees"));
			innerMap.put("slotPk", invoiceObj.get("slot_pk"));
			innerMap.put("courseName", invoiceObj.get("course_name"));
			innerMap.put("coursePk", invoiceObj.get("coursepk"));
			
			float timeSlotLength = ((List<String>) JSONUtil.jsonToObject(invoiceObj.get("slot_pk")+"", List.class)).size();
			innerMap.put("timeSlotLength", timeSlotLength);
			
			float duration = Float.valueOf((invoiceObj.get("duration")+"").split(" ")[0]);
			innerMap.put("feePerMin", Float.valueOf(invoiceObj.get("fees")+"") / duration);
			innerMap.put("actualAmont", (Float.valueOf(invoiceObj.get("fees")+"") / duration) * (timeSlotLength*15));
			
			if(mainMap == null) {
				mainMap = new HashMap<>();
				mainMap.put(invoiceObj.get("course_name")+"-"+invoiceObj.get("coursepk"), innerMap);
				//System.out.println("date:"+invoiceObj.get("date"));
				respMap.put(invoiceObj.get("date")+"", mainMap);
				
			} else {
				if(respMap.containsKey(invoiceObj.get("date"))) {
					//HashMap<String, Object> tempDetail = (HashMap<String, Object>) respMap.get(invoiceObj.get("date"));
					if(mainMap.containsKey(invoiceObj.get("course_name")+"-"+invoiceObj.get("coursepk"))) {
						HashMap<String, Object> tempDetail = (HashMap<String, Object>)
								mainMap.get(invoiceObj.get("course_name")+"-"+invoiceObj.get("coursepk"));
						String slots = tempDetail.get("slotPk")+"";
						tempDetail.put("slotPk", slots+"-"+invoiceObj.get("slot_pk"));
						
						float slotLength = (float) tempDetail.get("timeSlotLength");
						tempDetail.put("timeSlotLength", slotLength + timeSlotLength);
						
						tempDetail.put("actualAmont", (Float.valueOf(invoiceObj.get("fees")+"") / duration) *
								((slotLength + timeSlotLength)*15));
						
					}else {
						mainMap.put(invoiceObj.get("course_name")+"-"+invoiceObj.get("coursepk"), innerMap);
					}
					
				} else {
					mainMap = new HashMap<>();
					mainMap.put(invoiceObj.get("course_name")+"-"+invoiceObj.get("coursepk"), innerMap);
					//System.out.println("date:"+invoiceObj.get("date"));
					respMap.put(invoiceObj.get("date")+"", mainMap);
				}
			}
		}
		
		//System.out.println("respMap::"+JSONUtil.objectToJson(respMap));
		List<Object> dataListMain = DBServices.get("FROM EOStudentInvoiceMain WHERE isActive = true "
          		+ "AND month = '"+month+"' AND year ='"+year+"' AND eoStudentUser ="+eoStudentUser);
		
		//System.out.println("dataListMain::"+dataListMain);
			
		String stdFullNameEng = "";
		String stdFullNameJap = "";
		HashMap<String, Object> MainInnerMap = new HashMap<>();
		long eoStudentInvoiceMainPk = 0;
		for (Object obj : dataListMain) {

			EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) obj;
			List<EOStudentInvoiceDetail> detailArray = (List<EOStudentInvoiceDetail>) eoStudentInvoiceMain.eoStudentInvoiceDetails;
			List<EOStudentInvoiceEditable> editableDetails = (List<EOStudentInvoiceEditable>) eoStudentInvoiceMain.eoStudentInvoiceEditable;

			eoStudentInvoiceMainPk = eoStudentInvoiceMain.primaryKey;
			stdFullNameEng = eoStudentInvoiceMain.eoStudentUser.getFullName();
			stdFullNameJap = eoStudentInvoiceMain.eoStudentUser.getFullNameJapanese();
			MainInnerMap.put("primaryKey", eoStudentInvoiceMain.primaryKey + "");
			MainInnerMap.put("status", eoStudentInvoiceMain.status);
			MainInnerMap.put("eoStudentUser", eoStudentInvoiceMain.eoStudentUser.primaryKey + "");
			MainInnerMap.put("studentfullname", eoStudentInvoiceMain.eoStudentUser.getFullName());
			MainInnerMap.put("studentfullnameJap", eoStudentInvoiceMain.eoStudentUser.getFullNameJapanese());
			MainInnerMap.put("month", eoStudentInvoiceMain.month);
			MainInnerMap.put("year", eoStudentInvoiceMain.year);
			MainInnerMap.put("total", eoStudentInvoiceMain.total);
			MainInnerMap.put("depositAmount", eoStudentInvoiceMain.depositAmount);
			MainInnerMap.put("dueAmount", eoStudentInvoiceMain.dueAmount);
			MainInnerMap.put("consumptionTax", eoStudentInvoiceMain.consumptionTax);
			MainInnerMap.put("cancellationAmount", eoStudentInvoiceMain.cancellationAmount);
			MainInnerMap.put("grandTotal", eoStudentInvoiceMain.grandTotal);
			MainInnerMap.put("invoiceNo", eoStudentInvoiceMain.invoiceNo);
			MainInnerMap.put("isActive", eoStudentInvoiceMain.isActive);
			MainInnerMap.put("dueDate", eoStudentInvoiceMain.dueDate);

			List<HashMap<String, Object>> mainTempList = new ArrayList<>();
			List<HashMap<String, Object>> editableList = new ArrayList<>();

			if (detailArray != null && detailArray.size() > 0) {
				for (EOStudentInvoiceDetail eoStudentInvoiceDetail : detailArray) {
					HashMap<String, Object> mainTempMap = new HashMap<>();

					mainTempMap.put("primaryKey", eoStudentInvoiceDetail.primaryKey + "");
					mainTempMap.put("lkMusicType", eoStudentInvoiceDetail.lkMusicType.primaryKey + "");
					if (eoStudentInvoiceDetail.lkClassDuration != null) {
						mainTempMap.put("lkClassDuration", eoStudentInvoiceDetail.lkClassDuration.primaryKey + "");
						mainTempMap.put("sessionDuration", eoStudentInvoiceDetail.lkClassDuration.duration);
					}
					mainTempMap.put("fees", eoStudentInvoiceDetail.fees);
					mainTempMap.put("feeType", eoStudentInvoiceDetail.feeType);
					mainTempMap.put("session", eoStudentInvoiceDetail.session);
					mainTempMap.put("eoCourses", eoStudentInvoiceDetail.eoCourses.primaryKey);
					mainTempMap.put("courseName", eoStudentInvoiceDetail.eoCourses.courseName);
					mainTempMap.put("totalMins", eoStudentInvoiceDetail.totalMins);

					mainTempList.add(mainTempMap);
				}
				MainInnerMap.put("attributeList", mainTempList);
			}

			if (editableDetails != null && editableDetails.size() > 0) {
				for (EOStudentInvoiceEditable eoStudentInvoiceEditable : editableDetails) {
					HashMap<String, Object> mainTempMap = new HashMap<>();

					if (eoStudentInvoiceEditable.isActive == true) {
						mainTempMap.put("primaryKey", eoStudentInvoiceEditable.primaryKey + "");
						mainTempMap.put("amount", eoStudentInvoiceEditable.amount);
						mainTempMap.put("description", eoStudentInvoiceEditable.description);
						mainTempMap.put("month", eoStudentInvoiceEditable.month);
						mainTempMap.put("year", eoStudentInvoiceEditable.year);
						mainTempMap.put("isActive", eoStudentInvoiceEditable.isActive);

						editableList.add(mainTempMap);
					}
				}
				if (editableList != null && editableList.size() > 0) {
					MainInnerMap.put("editable", editableList);
				}
			}
		}

		//System.out.println("MainInnerMap::"+JSONUtil.objectToJson(MainInnerMap));
  		//List<HashMap<String, Object>> attributeList = (List<HashMap<String, Object>>) MainInnerMap.get("attributeList");
  		List<HashMap<String, Object>> editable = (List<HashMap<String, Object>>) MainInnerMap.get("editable");

		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream((pdfStorePathUrl) + ".pdf"));
		//PdfWriter.getInstance(document, new FileOutputStream("E:/invoice1.pdf"));
		document.open();
		BaseFont bf;
		Font font;
		Font fontBig;
		Font fontRed;
		Font fontBold;
		
		bf = BaseFont.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
		font = new Font(bf, 9);
		fontRed = new Font(bf, 9);
		fontRed.setColor(BaseColor.RED);
		fontBig = new Font(bf, 14);
		fontBig.setStyle("bold");
		fontBold = new Font(bf ,9);
		fontBold.setStyle("bold");

		Image suwayamaLogo = Image.getInstance(pdfStorePath+seperator+"Suwayama.PNG");
		//Image suwayamaLogo = Image.getInstance("E:/Suwayama.PNG");

		suwayamaLogo.scaleToFit(150, 70);
		suwayamaLogo.setAlignment(0);
		suwayamaLogo.setBorder(Rectangle.BOX);
		suwayamaLogo.setUseVariableBorders(true);
		suwayamaLogo.setBorderWidth(1);
		document.add(suwayamaLogo);

		document.add(Chunk.NEWLINE);

		Chunk monthYear = new Chunk(pdfContents.get("Invoice") + " (Invoice)", fontBig); // Invoice - year
		monthYear.setUnderline(0.1f, -2f);

		Phrase phrase1 = new Phrase();
		phrase1.add(monthYear);
		Paragraph monthYearD = new Paragraph();
		monthYearD.add(phrase1);
		monthYearD.setAlignment(Element.ALIGN_CENTER);
		document.add(monthYearD);
		document.add(Chunk.NEWLINE);

		String invoiceNo = "";
		if (MainInnerMap.get("invoiceNo") != null) {
			invoiceNo = (String) MainInnerMap.get("invoiceNo");
		}
		PdfPTable detailTable = new PdfPTable(4);
		detailTable.setWidthPercentage(100);
		detailTable.setWidths(new float[] { 50F, 113.8F, 168.8F, 190.4F });
		//address line1 and date
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Date") + " (Date): ", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(DateUtil.formatAsDDMMYYY(DateUtil.formatedCurrentDate())+"", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L1")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		
		//address line2 and invoice number
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Invoice No") + "(Invoice  No):", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(invoiceNo+"", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L2")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		
		//address line3 and due date
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Due Date") + "(Due Date):", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(DateUtil.formatAsDDMMYYY(MainInnerMap.get("dueDate")+""), PdfPCell.ALIGN_LEFT, fontRed, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L3")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		
		//address line4
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L4")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		document.add(detailTable);
		
		document.add(Chunk.NEWLINE);
		PdfPTable nameTable = new PdfPTable(5);
		nameTable.setWidthPercentage(100);
		nameTable.setWidths(new float[] { 50F, 113.8F, 168.8F, 75F, 115.4F });
		
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell(pdfContents.get("Student Name")+" (StudentName)", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell(stdFullNameJap.split(" ")[0]+new Chunk(" ("+stdFullNameEng.split(" ")[1]+")", fontBold), PdfPCell.ALIGN_LEFT, font, true));
		nameTable.addCell(getCell(stdFullNameJap.split(" ")[1]+" ("+stdFullNameEng.split(" ")[0]+")", PdfPCell.ALIGN_LEFT, font, true));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("Last Name", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("First Name", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		
		document.add(nameTable);
		document.add(Chunk.NEWLINE);

		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 50F, 113.8F, 168.8F,75F, 50F, 65.4F });
		
		NumberFormat myFormat = NumberFormat.getInstance();
		myFormat.setGroupingUsed(true);
		// table header starts
		// Item No.
		PdfPCell blankcell = new PdfPCell();
		PdfPCell c1 = new PdfPCell(new Phrase(pdfContents.get("Item No") + "(Item Number)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//date
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Date")+" (Date)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);
		
		//item description
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Item Description")+" (Description)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//Unit Price
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Unit price") +" (Unit Price)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//Unit
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Unit")+" (Unit)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//Amount
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Amount")+" (Amount)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);
		//Table Header Ends
		
			 
		//Table content Starts
		//adding all the attributes
		int itemNo = 0;
		float totalPayableAmount = 0;
		for(Map.Entry<String, Object> entry1 : respMap.entrySet()) {
			for(Map.Entry<String, Object> subEntry : ((HashMap<String, Object>)entry1.getValue()).entrySet()) {
				itemNo++;
				HashMap<String, Object> dataMap = (HashMap<String, Object>) subEntry.getValue();
				totalPayableAmount += (float)dataMap.get("actualAmont");
				
				//item no
				c1 = new PdfPCell(new Phrase(itemNo+"",font));
              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
              	table.addCell(c1);
              	
              	//date
              	c1 = new PdfPCell(new Phrase(DateUtil.formatAsDDMMYYY(dataMap.get("date")+""),font));
              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
              	table.addCell(c1);
              	
              	//item description
              	c1 = new PdfPCell(new Phrase(dataMap.get("courseName")+"("+Math.round((Float.valueOf(dataMap.get("timeSlotLength")+"") * 15))+" min)",font));
              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
              	table.addCell(c1);
              	
              	//Unit Price
              	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(dataMap.get("actualAmont")+""))),font));
              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
              	table.addCell(c1);
              	
              	//Unit
              	c1 = new PdfPCell(new Phrase("1",font));
              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
              	table.addCell(c1);
              	
              	//Amount
              	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(dataMap.get("actualAmont")+""))),font));
              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
              	table.addCell(c1);
			}
		}
		
		
		// Editable amount
		if(MainInnerMap.get("editable") != null){
			for(HashMap<String, Object> editObj :(List<HashMap<String, Object>>)MainInnerMap.get("editable")){
				
				totalPayableAmount += Float.valueOf(editObj.get("amount")+"");
				//
				c1 = new PdfPCell(new Phrase("", font));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(c1);

				//
				c1 = new PdfPCell(new Phrase("", font));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(c1);

				//
				c1 = new PdfPCell(new Phrase(editObj.get("description")+"", font));
				c1.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(c1);

				// total
				c1 = new PdfPCell(new Phrase("", font));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(c1);

				//
				c1 = new PdfPCell(new Phrase("", font));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(c1);

				// Amount
				c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(editObj.get("amount")+""))), font));
				c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(c1);
			}
		}
		
		
		//System.out.println("MainInnerMap::"+JSONUtil.objectToJson(MainInnerMap));
		//Cancellation Amount
		if(MainInnerMap.get("cancellationAmount") != null && Float.valueOf(MainInnerMap.get("cancellationAmount")+"") > 0){
			totalPayableAmount += Float.valueOf(MainInnerMap.get("cancellationAmount")+"");
			
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase(pdfContents.get("Cancellation Amount")+" (Cancellation Amount)", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// total
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);

			// Amount
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(MainInnerMap.get("cancellationAmount")+""))), font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
		}
		      
		float consumptionTax = Float.valueOf((MainInnerMap.get("consumptionTax")+""));
		float consumptionValue = Float.valueOf((MainInnerMap.get("consumptionTax")+"")) / 100;
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		//total amount
		//
		c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//
      	c1 = new PdfPCell(new Phrase(pdfContents.get("Total")+" (Total)",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//total
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
      	table.addCell(c1);
      	
      	//Amount
      	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(totalPayableAmount+""))) , font));
      	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
      	table.addCell(c1);
      	
		// Consumption tax 
		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase(pdfContents.get("Consumption Tax")+"("+consumptionTax+"%)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);

		// Amount
		c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf((totalPayableAmount * consumptionValue)+""))),font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);
		
		
		// Amount Payable
		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase(pdfContents.get("Amount Payable") + " (Amount Payable)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);

		// Amount
		c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+ myFormat.format(Math.round(Float.valueOf(((totalPayableAmount * consumptionValue) +totalPayableAmount)+""))), fontBold));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);
		
		table.addCell(blankcell);
		
		
		//cancellation 100% 
		String qry100 = "SELECT SUM(td.cancellation_amount), td.cancellation_percentage FROM eotimeslot t "
				+" LEFT JOIN eotime_slot_allocation td ON t.primary_key = td.eotimeslot_primary_key"
				+" WHERE t.date  BETWEEN '"+startDate+"' AND '"+endDate+"'"
				+" AND eostudentuser_primary_key ="+eoStudentUser+" AND td.cancellation_percentage ='100' "
				+" GROUP BY td.cancellation_percentage";
		List<HashMap<String, Object>> cancellation100 = DBServices.getNativeQueryResult(qry100);
				
		//cancellation 50% 
		String qry50 = "SELECT SUM(td.cancellation_amount), td.cancellation_percentage FROM eotimeslot t "
				+" LEFT JOIN eotime_slot_allocation td ON t.primary_key = td.eotimeslot_primary_key"
				+" WHERE t.date  BETWEEN '"+startDate+"' AND '"+endDate+"'"
				+" AND eostudentuser_primary_key ="+eoStudentUser+" AND td.cancellation_percentage ='50' "
				+" GROUP BY td.cancellation_percentage";
		List<HashMap<String, Object>> cancellation50 = DBServices.getNativeQueryResult(qry50);
		if((cancellation50 != null && cancellation50.size() > 0) ||
				cancellation100 != null && cancellation100.size() > 0){
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
			table.addCell(blankcell);
		}
		if(cancellation50 != null && cancellation50.size() > 0){
			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase(pdfContents.get("Cancellation 50")+" (50% Cancellation)", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
			
			//
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+(cancellation50.get(0).get("sum"))+"", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
		}
		
		if(cancellation100 != null && cancellation100.size() > 0){
			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase(pdfContents.get("Cancellation 100")+" (100% Cancellation)", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);

			//
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
						
			//
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+ myFormat.format(Math.round(Float.valueOf((cancellation100.get(0).get("sum"))+""))) , font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
		}
	
		document.add(table);
		document.add(Chunk.NEWLINE);

		Paragraph Line0 = new Paragraph((String) pdfContents.get("Payment Details"), font); // ApprovedBy
		Line0.setAlignment(Element.ALIGN_LEFT);
		document.add(Line0);
		Paragraph Line00 = new Paragraph("(Please check the above details and transfer the amount to the following bank account)", font); 
		Line00.setAlignment(Element.ALIGN_LEFT);
		document.add(Line00);
		document.add(Chunk.NEWLINE);
		
		Paragraph Line1 = new Paragraph((String) pdfContents.get("Invoice Bottom 1"), font); // ApprovedBy
		Line1.setAlignment(Element.ALIGN_LEFT);
		document.add(Line1);

		Paragraph Line2 = new Paragraph((String) pdfContents.get("Invoice Bottom 2"), font); // Suwayama
																								// Music
																								// Academy
		Line2.setAlignment(Element.ALIGN_LEFT);
		document.add(Line2);

		Paragraph Line3 = new Paragraph((String) pdfContents.get("Invoice Bottom 3"), font); // Suwayama
																								// Music
																								// Academy
		Line3.setAlignment(Element.ALIGN_LEFT);
		document.add(Line3);

		Paragraph Line4 = new Paragraph((String) pdfContents.get("Invoice Bottom 4"), font); // Suwayama
																								// Music
																								// Academy
		Line4.setAlignment(Element.ALIGN_LEFT);
		document.add(Line4);

		Paragraph Line5 = new Paragraph((String) pdfContents.get("Invoice Bottom 5"), font); // Suwayama
																								// Music
																								// Academy
		Line5.setAlignment(Element.ALIGN_LEFT);
		document.add(Line5);
		
		document.add(Chunk.NEWLINE);
		Paragraph LineA = new Paragraph((String) pdfContents.get("Payment Details for Student"), font); // ApprovedBy
		LineA.setAlignment(Element.ALIGN_LEFT);
		document.add(LineA);
		Paragraph LineB = new Paragraph("(For cash payment, please pay at the next class)", font); 
		LineB.setAlignment(Element.ALIGN_LEFT);
		document.add(LineB);
		
		document.close();
		
		
		if(!eoPdfPrimaryKey.equalsIgnoreCase("") && pdfObject != null && pdfObject.size() > 0){	
			EOPdf eoPdf = (EOPdf) EOObject.getObjectByPK("EOPdf", eoPdfPrimaryKey);
			HashMap<String, Object> pdfMap = new HashMap<>();
			pdfMap.put("updatedDate", DateUtil.formatedCurrentDate());
			pdfMap.put("saveNo", (eoPdf.saveNo) + 1);
			pdfMap.put("pdfUrl", pdfUrl);

			if (stats.equalsIgnoreCase("C")) {
				pdfMap.put("isActive", false);
			} else {
				pdfMap.put("isActive", true);
			}
			DBServices.update(EOObject.updateObject(eoPdf, pdfMap));
			EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain) EOObject
					.getObjectByPK("EOStudentInvoiceMain", eoStudentInvoiceMainPk + "");
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("eoPdf", eoPdf.primaryKey);
			DBServices.update(EOObject.updateObject(eoStudentInvoiceMain, innerMap));
		}
        else{									
        	HashMap<String, Object> pdfMap = new HashMap<>();
        	pdfMap.put("createdDate",DateUtil.formatedCurrentDate());
        	pdfMap.put("saveNo", 1);
        	pdfMap.put("entityName", "EOStudentInvoiceMain");
        	pdfMap.put("displayName", displayName);
        	pdfMap.put("pdfUrl", pdfUrl);
        	pdfMap.put("type", "pdf");
        	pdfMap.put("headerPk", eoStudentInvoiceMainPk);
        	pdfMap.put("pdfStorePath", pdfStorePath);
        	pdfMap.put("className", "EOPdf");
        	DBServices.create(EOObject.createObject(pdfMap));
        	EOPdf eoPdf = (EOPdf)EOObject.getLatestObject("EOPdf");
        	EOStudentInvoiceMain eoStudentInvoiceMain = (EOStudentInvoiceMain)
        			EOObject.getObjectByPK("EOStudentInvoiceMain", eoStudentInvoiceMainPk+"");
        	HashMap<String, Object> innerMap = new HashMap<>();
        	innerMap.put("eoPdf", eoPdf.primaryKey);
        	DBServices.update(EOObject.updateObject(eoStudentInvoiceMain, innerMap));
        }
	
		if(status.equalsIgnoreCase("Cancelled")) {
			return Response.status(201).entity(JSONUtil.objectToJson("Cancelled")).build();
		} else {
			return Response.status(201).entity(JSONUtil.objectToJson(pdfUrl + ".pdf")).build();
		}
	}
	
	@POST
	@Path("/createSalaryPdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createSalaryPdf(HashMap<String, Object> map)
			throws DocumentException,IOException,Exception {
		/*System.out.println("working directory:"+System.getProperty("user.dir"));*/
		String month = (String) map.get("month");
		String year = (String) map.get("year");
		String eoTeacherUser = (String) map.get("eoTeacherUser");
		String teacherfullnamePdf = (String) map.get("teacherfullnamePdf");
		String teacherfullname = (String) map.get("teacherfullname");
		String status = (String) map.get("status");
		String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");
		
		String qr = "SELECT key , value FROM eopdf_json";
		List<HashMap<String, Object>> pdfDetails = DBServices.getNativeQueryResult(qr);
		
		HashMap<String, Object> pdfContents = new HashMap<>();
		for(HashMap<String, Object> obj : pdfDetails){
			pdfContents.put((String) obj.get("key"),obj.get("value"));
		}
		
		String displayName = "EOGenerateSlipDetail_"+month+"_"+year+"_"+eoTeacherUser;
		
		String pdfUrl = teacherfullname+"_"+month+"_"+year+"_"+eoTeacherUser;
		List<Object> pdfObject = DBServices.get("FROM EOPdf WHERE displayName = '"+displayName+"' AND isActive = true");
		String eoPdfPrimaryKey = ""; 
		if(pdfObject != null && pdfObject.size() > 0){
			EOPdf eoPdf = (EOPdf) pdfObject.get(0);
			eoPdfPrimaryKey = eoPdf.primaryKey+"";
		}
		
		String seperator = System.getProperty("file.separator");
		String pdfStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"+ seperator + "SMA";
		
		String pdfStorePathUrl = pdfStorePath +seperator+ pdfUrl;
		
		//query for getting data by date range
		
		/*String strQuery = "SELECT d.day, d.date, CONCAT(d.start_time, '-' ,d.end_time) as time,ld.duration,"
					 +" ts.transport_amount, d.slot_pk, c.course_name,  c.primary_key as coursepk, "
					 +" CONCAT(su.last_name, ' ', su.first_name) studentfullname,ts.primary_key as salpk,"
					 +" CONCAT(su.last_name_jap, ' ', su.first_name_jap) studentfullnamejap,"
					 +" su.primary_key as studentpk , ts.amount FROM eodefined_slot d"
					 +" LEFT JOIN eobatch b ON eobatch_primary_key = b.primary_key"
					 +" LEFT JOIN eocourses c ON b.eocourses_primary_key = c.primary_key"
					 +" LEFT JOIN lkclass_duration ld ON c.lkclassduration_primary_key = ld.primary_key"
					 +" LEFT JOIN eoteacher_user tu ON tu.primary_key = d.eoteacheruser_primary_key"
					 +" LEFT JOIN eostudent_user su ON su.primary_key = d.eostudentuser_primary_key"
					 +" LEFT JOIN eoteacher_salary ts ON ts.eoteacheruser_primary_key = d.eoteacheruser_primary_key"
					 +" WHERE d.eoteacheruser_primary_key = "+eoTeacherUser+" AND month = '"+month+"' AND year = "
					 		+ "'"+year+"'  AND  ts.is_active = true";*/
		
		String teacherQry = "SELECT CONCAT(first_name, ' ', last_name), salary_type FROM eoteacher_user WHERE primary_key = "+eoTeacherUser;
		String perMonthSalary = "";
		List<HashMap<String, Object>> teacherData = DBServices.getNativeQueryResult(teacherQry);
		List<HashMap<String, Object>> teacherSalary = new ArrayList<>();
		if(teacherData.get(0).get("salary_type").equals("Per month")){
			//per month
			String stq = "SELECT amount, salary_type, transport_amount FROM eoteacher_salary WHERE eoteacheruser_primary_key="+eoTeacherUser; 
			teacherSalary = DBServices.getNativeQueryResult(stq);
			if(teacherSalary != null && teacherSalary.size() > 0){
				perMonthSalary = teacherSalary.get(0).get("amount")+"";
			}
		}
		String strQuery = "SELECT t.primary_key, t.date, t.day,CONCAT(t.start_time, '-' ,t.end_time) as time, ta.eoteacheruser_primary_key,"
						+" c.primary_key as coursepk, c.course_name,CONCAT(s.last_name, ' ', s.first_name) studentfullname,"
						+" CONCAT(s.last_name_jap, ' ', s.first_name_jap) studentfullnamejap,ld.duration, ta.status,ta.cancellation_percentage,"
						+" ts.transport_amount, ts.primary_key as salpk,ts.amount, s.primary_key as studentpk,ts.salary_type"
						+" FROM eotimeslot t LEFT JOIN eotime_slot_allocation ta ON t.primary_key = ta.eotimeslot_primary_key"
						+" LEFT JOIN eostudent_user s ON s.primary_key = ta.eostudentuser_primary_key "
						+" LEFT JOIN eobatch b ON b.primary_key = ta.eobatch_primary_key"
						+" LEFT JOIN eocourses c ON c.primary_key = b.eocourses_primary_key"
						+" LEFT JOIN lkclass_duration ld ON c.lkclassduration_primary_key = ld.primary_key"
						+" LEFT JOIN eoteacher_salary ts ON ts.eoteacheruser_primary_key = ta.eoteacheruser_primary_key"
						+" WHERE ta.eoteacheruser_primary_key ="+eoTeacherUser+" AND t.date BETWEEN '"+startDate+"' AND '"+endDate+"' AND teacher_cancellation = false";
						/*+" AND teacher_cancellation = false AND (ta.status = 'Partially Allocated' OR ta.status = 'Allocated')";*/
	
		//System.out.println("strQuery::"+strQuery);
		List<HashMap< String, Object>> salaryData = DBServices.getNativeQueryResult(strQuery);
		//System.out.println("slaryData:"+JSONUtil.objectToJson(salaryData));
		int transportDays = 0;
		TreeMap<String, Object> respMap = new TreeMap<>(); 
		for(HashMap<String, Object> salaryObj : salaryData) {
			HashMap<String, Object> mainMap  = (HashMap<String, Object>) respMap.get(salaryObj.get("date"));
			
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put("date", salaryObj.get("date"));
			//innerMap.put("time", salaryObj.get("time"));
			innerMap.put("duration", salaryObj.get("duration"));
			innerMap.put("transport_amount", salaryObj.get("transport_amount"));
			innerMap.put("courseName", salaryObj.get("course_name"));
			innerMap.put("coursePk", salaryObj.get("coursepk"));
			innerMap.put("studentFullName", salaryObj.get("studentfullname"));
			innerMap.put("studentFullNameJap", salaryObj.get("studentfullnamejap"));
			innerMap.put("eoTeacherSalary", salaryObj.get("salpk"));
			innerMap.put("totalDuration", "15");

			String qry = "SELECT sd.primary_key, sd.amount, d.duration FROM eoteacher_salary_detail sd"
					+ " LEFT JOIN lkclass_duration d ON d.primary_key = sd.lkclassduration_primary_key"
					+ " WHERE sd.eoteachersalary_primary_key ="
					+ salaryObj.get("salpk") +" AND sd.eocourses_primary_key ="+salaryObj.get("coursepk");
			//System.out.println("qry::---"+qry);
			List<HashMap<String, Object>> salaryDetail = DBServices.getNativeQueryResult(qry);
			String trans = " SELECT DISTINCT(d.date) FROM eodefined_slot d "
					+ "WHERE d.eoteacheruser_primary_key = "+eoTeacherUser+" AND month ='"+month+"' AND year ='"+year+"'";
			
			List<HashMap<String, Object>> transport = DBServices.getNativeQueryResult(trans);
			transportDays = transport.size();
			innerMap.put("payableTransport", (Float.valueOf(salaryObj.get("transport_amount")+""))*(transport.size()));
			//float timeSlotLength = ((List<String>) JSONUtil.jsonToObject(salaryObj.get("slot_pk")+"", List.class)).size();
			float duration= 0;
			
			if(salaryDetail != null && salaryDetail.size() > 0){
				innerMap.put("amount", salaryDetail.get(0).get("amount"));
				duration = Float.valueOf((salaryDetail.get(0).get("duration")+"").split(" ")[0]);
				innerMap.put("feePerMin", Float.valueOf(salaryDetail.get(0).get("amount")+"") / duration);
				innerMap.put("actualAmount", (Float.valueOf(salaryDetail.get(0).get("amount")+"") / duration) *15);
						
			}else{
				innerMap.put("amount", salaryObj.get("amount"));
				innerMap.put("actualAmount", salaryObj.get("amount"));
				innerMap.put("feePerMin", 0);
			}
			if(salaryObj.get("status").equals("Cancelled")){
				innerMap.put("cancellationCount", "1");
				innerMap.put("totalCancellation", ((Float.valueOf(innerMap.get("amount")+"") / duration)*15)
						* ((Float.valueOf(salaryObj.get("cancellation_percentage")+""))/100));
				innerMap.put("cancellationSum", salaryObj.get("cancellation_percentage"));
				innerMap.put("cancellationPerCentSum", salaryObj.get("cancellation_percentage"));
				innerMap.put("cancellationPerCent", salaryObj.get("cancellation_percentage"));
			}else{
				innerMap.put("cancellationCount", 0);
				innerMap.put("totalCancellation", 0);
				innerMap.put("cancellationSum", 0);
				innerMap.put("cancellationPerCentSum", 0);
				innerMap.put("cancellationPerCent", 0);
			}
			
			String keyName = salaryObj.get("studentfullname")+"-"+salaryObj.get("studentpk")+"+"+salaryObj.get("course_name")+"_"+salaryObj.get("coursepk");
			if(mainMap == null) {
				mainMap = new HashMap<>();
				mainMap.put(keyName, innerMap);
				respMap.put(salaryObj.get("date")+"", mainMap);
				
			} else {
				if(respMap.containsKey(salaryObj.get("date"))) {
					if(mainMap.containsKey(keyName)) {
						HashMap<String, Object> tempDetail = (HashMap<String, Object>)mainMap.get(keyName);
						if(salaryObj.get("status").equals("Cancelled")){
							float cancellationAmount = (Float.valueOf(tempDetail.get("totalCancellation")+"")) 
									+ (((Float.valueOf(tempDetail.get("amount")+"") / duration)*15) * ((Float.valueOf(salaryObj.get("cancellation_percentage")+""))/100));
							
							float cancellationPerCentSum = Float.valueOf(tempDetail.get("cancellationPerCentSum")+"")+ Float.valueOf(salaryObj.get("cancellation_percentage")+"");						
							tempDetail.put("cancellationCount", Float.valueOf(tempDetail.get("cancellationCount")+"")+1);							
							tempDetail.put("cancellationPerCentSum",cancellationPerCentSum);
							tempDetail.put("cancellationPerCent", cancellationPerCentSum/(Float.valueOf(tempDetail.get("cancellationCount")+"")));
							
							float totalCancellation = (float) ((15* (Float.valueOf(tempDetail.get("cancellationCount")+"")))*(Float.valueOf(tempDetail.get("feePerMin")+"")) 
									* ((Float.valueOf(tempDetail.get("cancellationPerCent")+""))*.01) );
							tempDetail.put("totalCancellation", totalCancellation);
						}else{
							//tempDetail.put("totalCancellation", 0);
						}
						
						float totalDuration = Float.valueOf(tempDetail.get("totalDuration")+"") +15;
						if(salaryDetail != null && salaryDetail.size() > 0){
							tempDetail.put("totalDuration", totalDuration);
							tempDetail.put("actualAmount", (Float.valueOf(tempDetail.get("amount")+"") / duration) *totalDuration);
							
						}else{
							tempDetail.put("amount", salaryObj.get("amount"));
							tempDetail.put("actualAmount", salaryObj.get("amount"));
						}
					}else {
						mainMap.put(keyName, innerMap);
					}
					
				} else {
					mainMap = new HashMap<>();
					mainMap.put(keyName, innerMap);
					respMap.put(salaryObj.get("date")+"", mainMap);
				}
			}
		}
	
		//System.out.println("respMap:"+JSONUtil.objectToJson(respMap));
		
		Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream((pdfStorePathUrl)+".pdf"));
        //PdfWriter.getInstance(document, new FileOutputStream("E:/sal11.pdf"));
        
        document.open();
        document.open();
		BaseFont bf;
		Font font;
		Font fontRed;
		Font fontBig;
		Font fontBold;
		Font fontBigBold;

		bf = BaseFont.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
		font = new Font(bf, 9);
		fontRed = new Font(bf, 9);
		fontBig = new Font(bf, 13);
		fontRed.setColor(BaseColor.RED);
		fontBold = new Font(bf ,9);
		fontBold.setStyle("bold");
		fontBigBold = new Font(bf ,13);
		fontBigBold.setStyle("bold");
		
		Image suwayamaLogo =Image.getInstance(pdfStorePath+seperator+"Suwayama.PNG");
		//Image suwayamaLogo = Image.getInstance("E:/Suwayama.PNG");

		suwayamaLogo.scaleToFit(150, 70);
		suwayamaLogo.setAlignment(0);
		suwayamaLogo.setBorder(Rectangle.BOX);
		suwayamaLogo.setUseVariableBorders(true);
		suwayamaLogo.setBorderWidth(1);
		document.add(suwayamaLogo);

		document.add(Chunk.NEWLINE);

		//Pay slip
		Chunk paySlip = new Chunk(pdfContents.get("Salary Slip") + " (Pay Slip)", fontBigBold);
		paySlip.setUnderline(0.1f, -2f);
		document.add(Chunk.NEWLINE);
		//month year
		Chunk monthYear = new Chunk(pdfContents.get("Salary Slip") +" ("+ month+" "+year+")", fontBold);
		monthYear.setUnderline(0.1f, -2f);

		Phrase phrase1 = new Phrase();
		phrase1.add(paySlip);
		Paragraph para = new Paragraph();
		para.add(phrase1);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para); 
		
		Phrase phrase2 = new Phrase();
		phrase2.add(monthYear);
		Paragraph para2 = new Paragraph();
		para2.add(phrase2);
		para2.setAlignment(Element.ALIGN_CENTER);
		document.add(para2);
		document.add(Chunk.NEWLINE);
		
		PdfPTable detailTable = new PdfPTable(4);
		detailTable.setWidthPercentage(100);
		detailTable.setWidths(new float[] { 50F, 81.5F, 111.5F, 291.5F });
		//address line1 and date
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Date") + " (Date): ", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(DateUtil.formatAsDDMMYYY(DateUtil.formatedCurrentDate())+"", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L1")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		
		//address line2
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L2")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		
		//address line3
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, fontRed, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L3")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		
		//address line4
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		detailTable.addCell(getCell(pdfContents.get("Salary Address L4")+"", PdfPCell.ALIGN_LEFT, font, false));
		
		document.add(detailTable);
		
		document.add(Chunk.NEWLINE);
		PdfPTable nameTable = new PdfPTable(7);
		nameTable.setWidthPercentage(100);
		nameTable.setWidths(new float[] { 50F, 81.5F, 111.5F, 136.5F, 60F, 35F, 60F });
		
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell(pdfContents.get("Teacher")+" (Teacher)", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell(teacherfullnamePdf.split(" ")[0]+" ("+teacherfullname.split(" ")[1]+")", PdfPCell.ALIGN_LEFT, fontBold, true));
		nameTable.addCell(getCell(teacherfullnamePdf.split(" ")[1]+" ("+teacherfullname.split(" ")[0]+")", PdfPCell.ALIGN_LEFT, fontBold, true));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("Last Name", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("First Name", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		nameTable.addCell(getCell("", PdfPCell.ALIGN_LEFT, font, false));
		
		document.add(nameTable);
		document.add(Chunk.NEWLINE);

		PdfPTable table = new PdfPTable(7); 
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 50F, 81.5F, 111.5F, 136.5F, 60F, 35F, 60F });

		PdfPCell blankcell = new PdfPCell();

		NumberFormat myFormat = NumberFormat.getInstance();
		myFormat.setGroupingUsed(true);
		
		// Table header starts
		// item
		PdfPCell c1 = new PdfPCell(new Phrase((String) pdfContents.get("Item No")+" (Item Number)", font)); 
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Date
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Date")+" (Date)", font)); 
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//Item Description
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Item Description") +" (Description)", font)); 
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//Student Name
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Student Name")+" (Student Name)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		//Unit Price
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Unit price")+" (Unit Price)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);
		
		//Unit
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Unit")+" (Unit)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);
		
		//Amount
		c1 = new PdfPCell(new Phrase((String) pdfContents.get("Amount")+" (Amount)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);
		// Table header ends

		
		// Table content Start
		int itemNo = 0;
		float totalPayableAmount = 0;
		float transportFee = 0;
		float payableTransport = 0;
		if(respMap != null){
			for(Map.Entry<String, Object> entry1 : respMap.entrySet()) {
				for(Map.Entry<String, Object> subEntry : ((HashMap<String, Object>)entry1.getValue()).entrySet()) {
					
					//Per session
					HashMap<String, Object> dataMap = (HashMap<String, Object>) subEntry.getValue();
					totalPayableAmount += Math.round(Float.valueOf(dataMap.get("actualAmount")+""));
					transportFee = Float.valueOf(dataMap.get("transport_amount")+"");
					payableTransport = Float.valueOf(dataMap.get("payableTransport")+"");
					
					if(teacherData.get(0).get("salary_type").equals("Per month")){
						//Per month
					}else{
						itemNo++;
						//Per Session
						//item no
						c1 = new PdfPCell(new Phrase(itemNo+"",font));
		              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		              	table.addCell(c1);
		              	
		              	//date
		              	c1 = new PdfPCell(new Phrase(DateUtil.formatAsDDMMYYY(dataMap.get("date")+""),font));
		              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		              	table.addCell(c1);
		              	
		              	//item description
		              	c1 = new PdfPCell(new Phrase(dataMap.get("courseName")+"("+Math.round(Float.valueOf(dataMap.get("totalDuration")+""))+" Min)",font));
		              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		              	table.addCell(c1);
		              	
		              	//Student Name
		              	c1 = new PdfPCell(new Phrase(dataMap.get("studentFullNameJap")+"("+dataMap.get("studentFullName")+")",font));
		              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		              	table.addCell(c1);
		              	
		              	//Unit Price
		              	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(dataMap.get("actualAmount")+""))) ,font));
		              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		              	table.addCell(c1);
		              	
		              	//Unit
		              	c1 = new PdfPCell(new Phrase("1",font));
		              	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		              	table.addCell(c1);
		              	
		              	//Amount
		              	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(dataMap.get("actualAmount")+""))),font));
		              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		              	table.addCell(c1);
		              	
		              	if(!dataMap.get("totalCancellation").equals(0)){
		              		totalPayableAmount -= Math.round(Float.valueOf(dataMap.get("totalCancellation")+""));
		              		//for cancellation amount
		              		itemNo++;
		              		//item no
							c1 = new PdfPCell(new Phrase(itemNo+"",font));
			              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			              	table.addCell(c1);
			              	
			              	//date
			              	c1 = new PdfPCell(new Phrase(DateUtil.formatAsDDMMYYY(dataMap.get("date")+""),font));
			              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			              	table.addCell(c1);
			              	
			              	//item description
			              	c1 = new PdfPCell(new Phrase(dataMap.get("cancellationPerCent")+"% Cancellation"+
			              								"("+ Math.round(15*Float.valueOf(dataMap.get("cancellationCount")+""))+" Min)",font));
			              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			              	table.addCell(c1);
			              	
			              	//Student Name
			              	c1 = new PdfPCell(new Phrase(dataMap.get("studentFullNameJap")+"("+dataMap.get("studentFullName")+")",font));
			              	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			              	table.addCell(c1);
			              	
			              	//Unit Price
			              	c1 = new PdfPCell(new Phrase("-"+pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(dataMap.get("totalCancellation")+""))),fontRed));
			              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			              	table.addCell(c1);
			              	
			              	//Unit
			              	c1 = new PdfPCell(new Phrase("1",font));
			              	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			              	table.addCell(c1);
			              	
			              	//Amount
			              	c1 = new PdfPCell(new Phrase("-"+pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(dataMap.get("totalCancellation")+""))),fontRed));
			              	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			              	table.addCell(c1);
		              	}
					}
				}
			}
		}
		if(teacherData.get(0).get("salary_type").equals("Per month")){
		
			itemNo++;
			//Per month
			totalPayableAmount = Float.valueOf(perMonthSalary);
			//item no
			c1 = new PdfPCell(new Phrase(itemNo+"",font));
          	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
          	table.addCell(c1);
          	
          	//date
          	c1 = new PdfPCell(new Phrase("",font));
          	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
          	table.addCell(c1);
          	
          	//item description
          	c1 = new PdfPCell(new Phrase(pdfContents.get("Per month")+" (Per month)",font));
          	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
          	table.addCell(c1);
          	
          	//Student Name
          	c1 = new PdfPCell(new Phrase("",font));
          	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
          	table.addCell(c1);
          	
          	//Unit Price
          	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(perMonthSalary+""))),font));
          	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
          	table.addCell(c1);
          	
          	//Unit
          	c1 = new PdfPCell(new Phrase("1",font));
          	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
          	table.addCell(c1);
          	
          	//Amount
          	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(perMonthSalary+""))),font));
          	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
          	table.addCell(c1);
		}
		
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		table.addCell(blankcell);
		
		//Additional 1
		float add = 0;
		if(map.get("add") == null){
			add = 0;
		}else{
			add = Float.valueOf(map.get("add")+"");
		}
		
		if(add > 0){
			
			totalPayableAmount += add;
			// item no
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// date
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// item description
			c1 = new PdfPCell(new Phrase(map.get("addDesc")+"", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// Student Name
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// Unit Price
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(add+""))), font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);

			// Unit
			c1 = new PdfPCell(new Phrase("1", font));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			// Amount
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(add+""))), font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
		}
		
		// Additional 2
		float sub = 0;
		if(map.get("sub") == null){
			sub = 0;
		}else{
			sub = Float.valueOf(map.get("sub")+"");
		}
		if (sub > 0) {

			totalPayableAmount -= sub;
			// item no
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// date
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// item description
			c1 = new PdfPCell(new Phrase(map.get("subDesc")+"", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// Student Name
			c1 = new PdfPCell(new Phrase("", font));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(c1);

			// Unit Price
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(sub+""))), font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);

			// Unit
			c1 = new PdfPCell(new Phrase("1", font));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			// Amount
			c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(sub+""))), font));
			c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(c1);
		}
		      
		//total
		//item no
		c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//date
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//item description
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//Student Name
      	c1 = new PdfPCell(new Phrase(pdfContents.get("Total")+" (Total)",font));
      	c1.setHorizontalAlignment(Element.ALIGN_LEFT);
      	table.addCell(c1);
      	
      	//Unit Price
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
      	table.addCell(c1);
      	
      	//Unit
      	c1 = new PdfPCell(new Phrase("",font));
      	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
      	table.addCell(c1);
      	
      	//Amount
      	c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(totalPayableAmount+""))),font));
      	c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
      	table.addCell(c1);
      	
      	
      	//School visits
		// item no
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// date
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// item description
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Student Name
		c1 = new PdfPCell(new Phrase(pdfContents.get("School Visits") + " (School Visits)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Unit Price
		c1 = new PdfPCell(new Phrase(transportDays+"", font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);

		// Unit
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		// Amount
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);
		
		
		// Transport Fee
		// item no
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// date
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// item description
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Student Name
		c1 = new PdfPCell(new Phrase(pdfContents.get("Transport Fee") + " (Transportation Fee)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Unit Price
		c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(transportFee+""))), font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);

		// Unit
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		// Amount
		c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf(payableTransport+""))), font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);

		
		// Amount Payable
		// item no
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// date
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// item description
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Student Name
		c1 = new PdfPCell(new Phrase(pdfContents.get("Amount Payable") + " (Amount Payable)", font));
		c1.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(c1);

		// Unit Price
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);

		// Unit
		c1 = new PdfPCell(new Phrase("", font));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		// Amount
		c1 = new PdfPCell(new Phrase(pdfContents.get("Yen")+""+myFormat.format(Math.round(Float.valueOf((totalPayableAmount+payableTransport)+""))), fontBold));
		c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(c1);
				
		document.add(table);
		// Table content ends
		document.close();
		
		if(!(boolean) map.get("isViewPdf")){
			if (!eoPdfPrimaryKey.equalsIgnoreCase("") && pdfObject != null && pdfObject.size() > 0) {
				// Update EOPdf
				EOPdf eoPdf = (EOPdf) EOObject.getObjectByPK("EOPdf", eoPdfPrimaryKey);

				HashMap<String, Object> pdfMap = new HashMap<>();
				pdfMap.put("updatedDate", DateUtil.formatedCurrentDate());
				pdfMap.put("saveNo", (eoPdf.saveNo) + 1);
				pdfMap.put("pdfUrl", pdfUrl);
				/*
				 * if(stats.equalsIgnoreCase("C")){ pdfMap.put("isActive", false);
				 * }else{ pdfMap.put("isActive", true); }
				 */
				DBServices.update(EOObject.updateObject(eoPdf, pdfMap));
				EOGenerateSlip eoGenerateSlip = (EOGenerateSlip) EOObject.getObjectByPK("EOGenerateSlip",
						map.get("primaryKey") + "");
				HashMap<String, Object> innerMap = new HashMap<>();
				innerMap.put("eoPdf", eoPdf.primaryKey);
				DBServices.update(EOObject.updateObject(eoGenerateSlip, innerMap));
			} else { 
				// Create EOPdf
				HashMap<String, Object> pdfMap = new HashMap<>();
				pdfMap.put("createdDate", DateUtil.formatedCurrentDate());
				pdfMap.put("saveNo", 1);
				pdfMap.put("entityName", "EOGenerateSlipDetail");
				pdfMap.put("displayName", displayName);
				pdfMap.put("pdfUrl", pdfUrl);
				pdfMap.put("type", "pdf");
				pdfMap.put("headerPk", map.get("primaryKey"));
				pdfMap.put("pdfStorePath", pdfStorePath);
				pdfMap.put("className", "EOPdf");
				DBServices.create(EOObject.createObject(pdfMap));
				EOPdf eoPdf = (EOPdf) EOObject.getLatestObject("EOPdf");
				EOGenerateSlip eoGenerateSlip = (EOGenerateSlip) EOObject.getObjectByPK("EOGenerateSlip",
						map.get("primaryKey") + "");
				HashMap<String, Object> innerMap = new HashMap<>();
				innerMap.put("eoPdf", eoPdf.primaryKey);
				DBServices.update(EOObject.updateObject(eoGenerateSlip, innerMap));
			}
		}
		
		
		return Response.status(201).entity(JSONUtil.objectToJson(pdfUrl+".pdf")).build();
		//return Response.status(201).entity(JSONUtil.objectToJson(respMap)).build();
	}
	
	@POST
	@Path("/createTimeTablePdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTimeTablePdf(HashMap<String, Object> map)
			throws DocumentException,IOException,Exception {
		
		List<String> dateList = (List<String>)map.get("date");
		String pdfUrl = (String)map.get("pdfUrl");

		TreeMap<String, Object> finalMap = new TreeMap<>();
		
		for(String dateObj: dateList){
			String str = "SELECT CONCAT(ds.date, '_', ds.day) as date, CONCAT(ds.start_time, '-', ds.end_time) as time, ds.eobatch_primary_key," 
					+" ds.eoroom_primary_key, ds.eostudentuser_primary_key,ds.eoteacheruser_primary_key, ds.lkmusictype_primary_key, "
					+" CONCAT (s.last_name_jap, ' ',s.first_name_jap) as studentname, t.first_name_jap as teachername,r.room_name,t.background_color, t.color_code" 
					+" FROM eodefined_slot ds"
					+" LEFT JOIN eostudent_user s ON s.primary_key = ds.eostudentuser_primary_key"
					+" LEFT JOIN eoteacher_user t ON t.primary_key = ds.eoteacheruser_primary_key"
					+" LEFT JOIN eomusic_room r ON r.primary_key = ds.eoroom_primary_key"
					+" WHERE ds.date = '"+dateObj+"'order by ds.primary_key ASC  ";
			
			
			List<HashMap<String, Object>> dataList = DBServices.getNativeQueryResult(str);
			String key = "";
			List<HashMap<String, Object>> mainList = new ArrayList<>();
			if(dataList != null && dataList.size() > 0){
				for(HashMap<String, Object> obj : dataList){
					mainList.add(obj);
					key = (String) obj.get("date");
				}
				finalMap.put(key, mainList);
			}
		}
		
		String seperator = System.getProperty("file.separator");
		String pdfStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"+ seperator + "SMA";
		
		String pdfStorePathUrl = pdfStorePath +seperator+ pdfUrl;
		Document document = new Document(PageSize.A4.rotate(),11,11,50,50);
		//document.setPageSize(PageSize.A4.rotate());
		PdfWriter writer = null;
		writer = PdfWriter.getInstance(document, new FileOutputStream(pdfStorePathUrl+".pdf"));
        /*PdfWriter.getInstance(document, new FileOutputStream("E:/sal.pdf"));*/
        
        document.open();
        BaseFont bf;
        Font font;
        
        document.setMargins(11, 11, 40, 40);
	       
        bf = BaseFont.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
        font = new Font(bf, 7);
        
        for(Map.Entry<String, Object> entry1 : finalMap.entrySet()){
        	List<HashMap<String, Object>> dataList = (List<HashMap<String, Object>>) entry1.getValue();
        	
        	PdfPTable table = new PdfPTable(dataList.size()+1); 
            table.setWidthPercentage(100);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);;
            float widthOfCell = 775 / (dataList.size());
            float[] widthList = new float[dataList.size()+1];
            widthList[0] =  45.0F;
            for (int i = 1; i < dataList.size()+1; i++) {
            	widthList[i] = widthOfCell;
			}

            table.setTotalWidth(widthList);
            table.setLockedWidth(true);
            String dateKey[] = entry1.getKey().split("_");
            String date = dateKey[0];
            String day = dateKey[1];
            PdfPCell c1 = new PdfPCell(new Phrase(date+"\n"+day,font));		//item no
            c1.setPaddingTop(10);
            c1.setPaddingBottom(10);
        	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
           	table.addCell(c1);
           	//c1.setFixedHeight(60F);
           	
            for(HashMap<String, Object> dataObj : dataList){
            	String roomName = "";
            	if(dataObj.get("room_name") != null){
            		
            		roomName = "\n" + (String) dataObj.get("room_name");
            	}
            	PdfPCell c2 = new PdfPCell(new Phrase((String)dataObj.get("time")+"\n"+
            			(String)dataObj.get("studentname")+"\n"+"("+(String)dataObj.get("teachername")+")"
            			+roomName,font));
	             
            	c2.setPaddingTop(10);
                c2.setPaddingBottom(10);
            	c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            	
            	String colorCode = "";
            	if(dataObj.get("color_code") != null) {
            		colorCode = (String) dataObj.get("color_code");
            	}else {
            		colorCode = "#eeeeee";
            	}
            	//colorCode = (String) dataObj.get("color_code");
            	c2.setBackgroundColor(WebColors.getRGBColor(colorCode.trim()));
            	//c2.setFixedHeight(60F);
               	table.addCell(c2);
               	document.add(table);
            }
        }
        
        /*Rectangle rectangle = new Rectangle(0f, 200f, 100f,100f);
        PdfContentByte canvas = writer.getDirectContent();
        rectangle.setBackgroundColor(new BaseColor(255, 0, 0)); // or whatever color you have
        
        rectangle.setBorder(Rectangle.BOX);
        rectangle.setUseVariableBorders(true);
        rectangle.setBorderWidth(2);
        rectangle.setBorderColor(BaseColor.GREEN);
        canvas.rectangle(rectangle);
        ColumnText ct = new ColumnText(canvas);
        ct.setSimpleColumn(0f, 200f, 100f,100f);
         
        ct.addElement(new Paragraph("kundan",font));
        ct.go();*/
        document.add(new Chunk(""));
        document.close();
		return Response.status(201).entity(JSONUtil.objectToJson(pdfUrl+".pdf")).build();
	}
	
	@POST
	@Path("/getStudentInvoicePdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStudentInvoicePdf(HashMap<String, Object> map)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		String month = (String)map.get("month");
		String year = (String)map.get("year");
		String stdPk = "";
		if(map.get("stdPk") != null){
			stdPk = (String)map.get("stdPk");
		}else{
			stdPk = (String)map.get("eoStudentUser");
		}
		//HashMap<String, Object> innerMap = new HashMap<>();
		String qryStr = "";
		if((map.get("className")+"").equalsIgnoreCase("EOGenerateSlip")){
			qryStr = "SELECT primary_key, month, year, eopdf_primary_key,eoteacheruser_primary_key "
					+ " FROM public.eogenerate_slip WHERE month = '"+month+"' AND"
					+ " year = '"+year+"' AND eoteacheruser_primary_key = "+map.get("eoTeacherUser");
			
		}else{
			qryStr = "SELECT primary_key, month, year, eopdf_primary_key,eostudentuser_primary_key "
					+ " FROM public.eostudent_invoice_main WHERE month = '"+month+"' AND"
					+ " year = '"+year+"' AND eostudentuser_primary_key = "+stdPk;
		}
		
		List<HashMap<String, Object>> list = DBServices.getNativeQueryResult(qryStr);
		String pdfUrl = "";
		if (list != null && list.size() > 0) {
			Object pdfkey= list.get(0).get("eopdf_primary_key");
			String qry = "select primary_key , image_url FROM eopdf  where  primary_key ="+ pdfkey;
			List<HashMap<String, Object>> qrylist = DBServices.getNativeQueryResult(qry);
			
			if (qrylist != null && qrylist.size() > 0) {
				pdfUrl = qrylist.get(0).get("image_url")+".pdf";
			}
		}
		return Response.status(201).entity(JSONUtil.objectToJson(pdfUrl)).build();
	}
	
	
	@POST
	@Path("/expenseSheetPdf")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response expenseSheetPdf(HashMap<String, Object> map)
			throws DocumentException,IOException,Exception {
		/*System.out.println("working directory:"+System.getProperty("user.dir"));*/
		
		String month = (String) map.get("month");
		String year = (String) map.get("year");
		
		HashMap<String, Object> incomeData = (HashMap<String, Object>)map.get("income");
		List<HashMap<String, Object>> salaryList = (List<HashMap<String, Object>>)map.get("salary");
		List<HashMap<String, Object>> expenseList = (List<HashMap<String, Object>>)map.get("expense");
		
		String qr = "SELECT key , value FROM eopdf_json";
		List<HashMap<String, Object>> pdfDetails = DBServices.getNativeQueryResult(qr);
		
		HashMap<String, Object> pdfContents = new HashMap<>();
		for(HashMap<String, Object> obj : pdfDetails){
			pdfContents.put((String) obj.get("key"),obj.get("value"));
		}
		//HashMap<String, Object> expenseData = RegisterExpensesController.getExpenseSheetData(map);
		String pdfUrl = "ExpenseSheet"+"_"+month+"_"+year;
		String seperator = System.getProperty("file.separator");
		String pdfStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"+ seperator + "SMA";
		String pdfStorePathUrl = pdfStorePath +seperator+ pdfUrl;
		Document document = new Document();
		PdfWriter writer= PdfWriter.getInstance(document, new FileOutputStream((pdfStorePathUrl)+".pdf"));
		//PdfWriter writer=  PdfWriter.getInstance(document, new FileOutputStream("E:/sal2.pdf"));
        
        document.open();
        BaseFont bf;
        Font font;
	       
        bf = BaseFont.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
		font = new Font(bf, 8);
		
		//System.out.println("Left margin:"+document.leftMargin());
		//System.out.println("Right margin:"+document.rightMargin());
		// Image.getInstance(pdfStorePath+seperator+"Suwayama.PNG");
		/*Image suwayamaLogo = Image.getInstance("E:/Suwayama.PNG");
		suwayamaLogo.scaleToFit(150, 70);
		suwayamaLogo.setAbsolutePosition(40, PageSize.A4.getHeight() - 90);
		suwayamaLogo.setBorder(Rectangle.BOX);
		suwayamaLogo.setUseVariableBorders(true);
		suwayamaLogo.setBorderWidth(1);
		document.add(suwayamaLogo);*/
		/* c1 = new PdfPCell(new Phrase((String) pdfContents.get("Total Amount"),font));				//total amount
           c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
           table.addCell(c1);*/
         
		//Income table
		PdfPTable table = new PdfPTable(5);
		table.setTotalWidth(261.5F) ;
		table.setLockedWidth(true);
		table.setWidths(new float[] { 70F, 21.5F, 60F, 50F, 60F });
		PdfPCell cell = new PdfPCell(new Phrase("INCOME"));
		cell.setColspan(5);
		cell.setHorizontalAlignment(1);
		table.addCell(cell);
		
		//Table 1 header 
		cell = new PdfPCell(new Phrase("Teacher Name" , font));
		cell.setBackgroundColor(WebColors.getRGBColor("#888888"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("SI No.", font));
		cell.setBackgroundColor(WebColors.getRGBColor("#888888"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Student Name", font));
		cell.setBackgroundColor(WebColors.getRGBColor("#888888"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Fees", font));
		cell.setBackgroundColor(WebColors.getRGBColor("#888888"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Course", font));
		cell.setBackgroundColor(WebColors.getRGBColor("#888888"));
		table.addCell(cell);
		
		BaseFont smFont;
        Font fontSm;
		smFont = BaseFont.createFont("KozMinPro-Regular", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
		fontSm = new Font(smFont, 7);
		
		//Table 1 Content
		int countTable1 = 0;
		double incomeTotal = 0;
		for(Map.Entry<String, Object> student : incomeData.entrySet()){
			HashMap<String, Object> stdData = (HashMap<String, Object>)student.getValue();
			countTable1++;
			String teacherName = "";
			int tempCount = 0;
			if(stdData.get("teacherJapNameList")!= null){
				for(Object std : (List<HashMap<String, Object>>)stdData.get("teacherJapNameList")){
					incomeTotal += Double.valueOf((stdData.get("grandTotal")+""));
					tempCount++;
					String array = (std+"").split("_")[1];
					String color = array.split("-")[0];
					teacherName += (std+"").split("_")[0];	
					if(((List<HashMap<String, Object>>)stdData.get("teacherJapNameList")).size() > tempCount){
						teacherName += ", ";
					}
				}
			}
			cell = new PdfPCell(new Phrase(teacherName, fontSm));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(countTable1+"", fontSm));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(stdData.get("studentFullName")+"", fontSm));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(stdData.get("grandTotal")!= null ? stdData.get("grandTotal")+"":"", fontSm));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(stdData.get("courseName") != null ? stdData.get("courseName")+"":"", fontSm));
			table.addCell(cell);
		}
		cell = new PdfPCell(new Phrase("Total", font));
		table.addCell(cell);
		table.addCell("");
		table.addCell("");
		cell = new PdfPCell(new Phrase((pdfContents.get("Yen")+"")+incomeTotal, font));
		table.addCell(cell);
		table.addCell("");
		table.writeSelectedRows(0, -1, document.left(), document.top(), writer.getDirectContent());
		
		// Expense table
		PdfPTable table2 = new PdfPTable(3);
		table2.setTotalWidth(261F) ;
		table2.setLockedWidth(true);
		table2.setWidths(new float[] { 90F, 90F, 81F });
		PdfPCell cell2 = new PdfPCell(new Phrase("EXPENSE"));
		cell2.setColspan(3);
		cell2.setHorizontalAlignment(1);
		table2.addCell(cell2);

		if(salaryList != null && salaryList.size() > 0){
			double salaryTotal = 0;
			//Salary Header
			cell2 = new PdfPCell(new Phrase("Teacher Name" , font));
			cell2.setBackgroundColor(WebColors.getRGBColor("#888888"));
			table2.addCell(cell2);
			cell2 = new PdfPCell(new Phrase("Salary", font));
			cell2.setBackgroundColor(WebColors.getRGBColor("#888888"));
			table2.addCell(cell2);
			cell2 = new PdfPCell(new Phrase("", font));
			cell2.setBackgroundColor(WebColors.getRGBColor("#888888"));
			table2.addCell(cell2);
			
			//salary content
			for(HashMap<String, Object> salary : salaryList){
				if(salary.get("total_salary") != null){
					salaryTotal += Double.valueOf(salary.get("total_salary")+"");
				}
				cell2 = new PdfPCell(new Phrase(salary.get("teacherfullname")+"", fontSm));
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase(salary.get("total_salary") != null ? salary.get("total_salary")+"":"", fontSm));
				table2.addCell(cell2);
				table2.addCell("");
			}
			cell2 = new PdfPCell(new Phrase("Total", font));
			table2.addCell(cell2);
			cell2 = new PdfPCell(new Phrase((pdfContents.get("Yen")+"")+salaryTotal, font));
			table2.addCell(cell2);
			table2.addCell("");
		}
		
		if(expenseList != null && expenseList.size() > 0){
			for(HashMap<String, Object> expense : expenseList){
				
				double expenseAmount = 0;
				//Expense Header
				expenseAmount += Double.valueOf(expense.get("amount") +"");
				
				cell2 = new PdfPCell(new Phrase(expense.get("accountType") +"", font));
				cell2.setBackgroundColor(WebColors.getRGBColor("#888888"));
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase("Amount", font));
				cell2.setBackgroundColor(WebColors.getRGBColor("#888888"));
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase("", font));
				cell2.setBackgroundColor(WebColors.getRGBColor("#888888"));
				table2.addCell(cell2);
				
				//Expense content
				
				cell2 = new PdfPCell(new Phrase(expense.get("accountName")+"", fontSm));
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase(expense.get("amount")+"", fontSm));
				table2.addCell(cell2);
				table2.addCell("");
				if(expense.get("subExpenseList") != null){
					for(HashMap<String, Object> subExpense : (List<HashMap<String, Object>>)expense.get("subExpenseList")){
						expenseAmount += Double.valueOf(subExpense.get("amount") +"");
						cell2 = new PdfPCell(new Phrase(subExpense.get("subAccountName")+"", fontSm));
						table2.addCell(cell2);
						cell2 = new PdfPCell(new Phrase(subExpense.get("amount")+"", fontSm));
						table2.addCell(cell2);
						table2.addCell("");
					}
				}
				
				cell2 = new PdfPCell(new Phrase("Total", font));
				table2.addCell(cell2);
				cell2 = new PdfPCell(new Phrase((pdfContents.get("Yen")+"")+expenseAmount, font));
				table2.addCell(cell2);
				table2.addCell("");
			}
		}
		table2.writeSelectedRows(0, -1, document.left() + 262F, document.top(), writer.getDirectContent());

		document.close();
		return Response.status(201).entity(JSONUtil.objectToJson(pdfUrl+".pdf")).build();
	}
	
	public PdfPCell getCell(String text, int alignment , Font font, Boolean borderBottom) {
	    PdfPCell cell = new PdfPCell(new Phrase(text,font));
	    cell.setPadding(1);
	    cell.setHorizontalAlignment(alignment);  
	    if(borderBottom){
	    	cell.setPaddingBottom(2);
	    	cell.setBorder(Rectangle.BOTTOM);
	    }else{
	    	cell.setBorder(PdfPCell.NO_BORDER);
	    }
	    
	    return cell;
	}
}

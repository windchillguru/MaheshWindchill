/**
 *** Purpose:  This java file used for creating the report in the temp folder.
 **** Implemented for ESR# 18726
 *** History: 	Created on 07-Jan-2016
 *** Author: Varsha P.L
 */
package ext.carrier.wc.esi;

import wt.epm.EPMDocument;
import wt.fc.WTObject;
import java.io.IOException;
import wt.util.WTException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import wt.change2.WTChangeActivity2;
import wt.doc.WTDocument;
import wt.iba.value.IBAHolder;
import ext.carrier.wc.change.changenotice.IBAReader;


public class CarrierESIReportDetailsForEPM{

	/**
	 * @param workbook
	 * @param wtObject
	 * @param cActivity
	 * @param rowcnt
	 * @return Method used for creating the Report Data for EPM and WTDocument
	 */
	public static HSSFWorkbook createReportDataForEPMandWTDoc(
			HSSFWorkbook workbook, WTObject wtObject,
			WTChangeActivity2 cActivity, int rowcnt) throws WTException,
			IOException {
		//System.out.println("****createReportDataForEPMandWTDoc***");
		String resObjType = "";
		String resObjName = "";
		String resObjNumber = "";
		String resObjPrtQual ="";
		String resObjLc = "";
		String resObjRev = "";
		String resObjIter = "";
		String resUSCategory = "";
		String resUSClassifierEmail = "";
		String resUSDateClassified = "";
		String resUSECCN = "";
		String resUSJurisdiction = "";
		String resUSRationale = "";
		String resUSSource = "";
		EPMDocument resultingEPM = null;
		WTDocument resultingDoc = null;

		if (wtObject instanceof EPMDocument) {
			resultingEPM = (EPMDocument) wtObject;
			resObjNumber = resultingEPM.getNumber();
			resObjName = resultingEPM.getName();
			resObjType = resultingEPM.getDocType().getDisplay();
			resObjLc = resultingEPM.getLifeCycleState().getDisplay();
			resObjRev = ((EPMDocument) resultingEPM).getVersionIdentifier()
					.getValue();
			resObjIter = ((EPMDocument) resultingEPM).getIterationIdentifier()
					.getValue();
			IBAHolder ibahldrPart = (IBAHolder) resultingEPM;
			IBAReader compreader = new ext.carrier.wc.change.changenotice.IBAReader(
					ibahldrPart);
			resObjPrtQual = (String) compreader.getValue("PART_QUALIFIER");
			resUSCategory = (String) compreader.getValue("US_CATEGORY");
			resUSClassifierEmail = (String) compreader
					.getValue("US_CLASSIFIERS_EMAIL");
			resUSDateClassified = (String) compreader
					.getValue("US_DATE_CLASSIFIED");
			resUSECCN = (String) compreader.getValue("US_ECCN");
			resUSJurisdiction = (String) compreader.getValue("US_JURISDICTION");
			resUSRationale = (String) compreader.getValue("US_RATIONALE");
			resUSSource = (String) compreader.getValue("US_SOURCE");

		}

		if (wtObject instanceof WTDocument) {
			resultingDoc = (WTDocument) wtObject;
			resObjNumber = resultingDoc.getNumber();
			resObjName = resultingDoc.getName();
			resObjType = resultingDoc.getType();			
			resObjLc = resultingDoc.getLifeCycleState().getDisplay();
			resObjRev = ((WTDocument) resultingDoc).getVersionIdentifier()
					.getValue();
			resObjIter = ((WTDocument) resultingDoc).getIterationIdentifier()
					.getValue();
			IBAHolder ibahldrPart = (IBAHolder) resultingDoc;
			IBAReader compreader = new ext.carrier.wc.change.changenotice.IBAReader(
					ibahldrPart);
			resUSCategory = (String) compreader.getValue("US_CATEGORY");
			resUSClassifierEmail = (String) compreader
					.getValue("US_CLASSIFIERS_EMAIL");
			resUSDateClassified = (String) compreader
					.getValue("US_DATE_CLASSIFIED");
			resUSECCN = (String) compreader.getValue("US_ECCN");
			resUSJurisdiction = (String) compreader.getValue("US_JURISDICTION");
			resUSRationale = (String) compreader.getValue("US_RATIONALE");
			resUSSource = (String) compreader.getValue("US_SOURCE");
		}

		HSSFSheet sheet = workbook.getSheetAt(1);
		HSSFRow row = sheet.createRow(rowcnt);

		HSSFCell cell = row.createCell(0);
		cell.setCellValue(resObjType);

		cell = row.createCell(1);
		cell.setCellValue(resObjNumber);

		cell = row.createCell(2);
		cell.setCellValue(resObjName);
		
		cell = row.createCell(3);
		if (resObjPrtQual != "") {
			cell.setCellValue(resObjPrtQual);
		} else {
			cell.setCellValue("N/A");
		}

		cell = row.createCell(4);
		cell.setCellValue(resObjRev + "." + resObjIter);

		cell = row.createCell(5);
		cell.setCellValue(resObjLc);

		cell = row.createCell(6);
		cell.setCellValue(cActivity.getNumber());

		cell = row.createCell(7);
		cell.setCellValue(cActivity.getLifeCycleState().getDisplay());

		cell = row.createCell(8);
		cell.setCellValue(cActivity.getName());

		cell = row.createCell(9);
		cell.setCellValue(resUSCategory);

		cell = row.createCell(10);
		cell.setCellValue(resUSClassifierEmail);

		cell = row.createCell(11);
		cell.setCellValue(resUSDateClassified);

		cell = row.createCell(12);
		cell.setCellValue(resUSECCN);
		cell = row.createCell(13);
		cell.setCellValue(resUSJurisdiction);

		cell = row.createCell(14);
		cell.setCellValue(resUSRationale);

		cell = row.createCell(15);
		cell.setCellValue(resUSSource);
		//System.out.println("****createReportDataForEPMandWTDoc END***");
		return workbook;
	}

}

/**
 *** Purpose:  This java file used for creating the report in the temp folder.
 **** Implemented for ESR# 18726
 *** History: 	Created on 07-Jan-2016
 *** Author: Varsha P.L
 *** Modified By Varsha P.L  on 12-Sep-2016
 **** ESR#47030,#47059
 */
package ext.carrier.wc.esi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import wt.epm.EPMDocument;
import java.util.Set;
import java.io.PrintWriter;
import java.util.HashSet;
import wt.epm.EPMDocumentType;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.fc.WTObject;
import wt.epm.navigator.CollectItem;
import wt.epm.navigator.EPMNavigateHelper;
import wt.epm.navigator.relationship.UIRelationships;
import java.io.File;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import wt.fc.PersistenceServerHelper;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.method.RemoteAccess;
import ext.carrier.wc.change.changenotice.IBAReader;
import ext.carrier.wc.esi.CarrierESIValidationReportAttach;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.part.WTPartUsageLink;
/*import com.ptc.windchill.esi.bom.BOMUtility;
import com.ptc.windchill.esi.tgt.ESITarget;
import com.ptc.windchill.esi.svc.ESIHelper;
import com.ptc.windchill.esi.tgt.ESITargetAssignmentLink;
import com.ptc.windchill.esi.utl.ESIException;*/
import ext.carrier.wc.esi.CarrierESIReportDetailsForEPM;

public class CarrierESIValidationReport implements RemoteAccess {

	public static String CLASSNAME = CarrierESIValidationReport.class.getName();
    private static final org.apache.log4j.Logger logger = wt.log4j.LogR.getLogger(CLASSNAME);
	public static Properties properties = null;
	public static String strDEBUG = null;
	public static boolean DEBUG = false;
	static File propertyFile;
	static Properties prop;
	/**
	 * Method used to check if VERBOSE ON to display message on MethodServer
	 * Log.
	 * 
	 * @param String
	 *            The string that has to be displayed on Console if Verbose is
	 *            true.
	 * @return void
	 * @exception
	 **/
	static {
		try {
			properties = ServiceProperties
					.getServiceProperties("WTServiceProviderFromProperties");
			strDEBUG = properties.getProperty("ext.carrier.wc.esi.DEBUG");
			DEBUG = Boolean.parseBoolean(strDEBUG);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		WTProperties wtProperties = null;
		try {
			wtProperties = WTProperties.getLocalProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String wtHome = wtProperties.getProperty("wt.codebase.location");
		propertyFile = new File(wtHome + File.separator + "ext"
				+ File.separator + "carrier" + File.separator + "wc"
				+ File.separator + "esi" + File.separator
				+ "esiReport.properties");

		prop = new Properties();
		try {
			prop.load(new FileInputStream(propertyFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "deprecation", "resource" })
	/**
	 *Method for checking the ECN number
	 *entered by the user in the URL
	 *Fetches the change activities and the resulting object details related to ECN.
	 *Checks whether the part is BOM or not.
	 *Check whether the BOM component is present in resulting object
	 */
	public static String checkECNNumber(String ecnNumber,HttpServletResponse response) throws Exception {
		logger.debug(CLASSNAME + "Entered checkECNNumber : "+ecnNumber);
		//System.out.println("Start of ECN Number Check");
		HSSFWorkbook report = new HSSFWorkbook();
		wt.change2.WTChangeOrder2 ecnObject = null;
		WTChangeActivity2 activity = null;
		int rowcnt = 6;
		int rowcnt2 = 6;
		String ecnNumUpCase = ecnNumber.toUpperCase();
		boolean BOMexists = false;
		wt.fc.WTObject wtObject = null;
		WTPart wtPart = null;
		ArrayList<String> headerList2 = new ArrayList<String>();
		ArrayList<String> headerList = new ArrayList<String>();
		String header1 = prop.getProperty("headerForPart");
		String header2 = prop.getProperty("headerForEPM");
		//System.out.println("Header details" + header1);
		StringTokenizer headerdetail = new StringTokenizer(header1, "|");
		while (headerdetail.hasMoreTokens()) {
			headerList.add(headerdetail.nextToken());
		}
		headerList.trimToSize();
		//System.out.println("Header details" + header2);
		StringTokenizer headerdetail2 = new StringTokenizer(header2, "|");
		while (headerdetail2.hasMoreTokens()) {
			headerList2.add(headerdetail2.nextToken());
		}
		headerList2.trimToSize();
		ArrayList<String> resObjList = new ArrayList<String>();
		//System.out.println("resObjList");
		//BOMUtility checkBOM = new BOMUtility();
		QuerySpec qsecnQuery = new QuerySpec(wt.change2.WTChangeOrder2.class);
		//wt.query.QuerySpec.appendWhere(WhereExpression, int) is deprecated
		//Updated for WC 11 Upgrade - ESR 59662
		/*qsecnQuery.appendWhere(new SearchCondition(
				wt.change2.WTChangeOrder2.class, "master>number",
				SearchCondition.EQUAL, ecnNumUpCase));*/
		qsecnQuery.appendWhere(new SearchCondition(
				wt.change2.WTChangeOrder2.class, "master>number",
				SearchCondition.EQUAL, ecnNumUpCase), new int[] {0});
		QueryResult qrecnResult = PersistenceServerHelper.manager
				.query(qsecnQuery);
		logger.debug(CLASSNAME + "qrecnResult size : "+qrecnResult.size());
		
		if (qrecnResult.size() > 0) {
			WTObject wtobject = (WTObject) qrecnResult.nextElement();
			if (wtobject instanceof WTChangeOrder2) {
				ecnObject = (wt.change2.WTChangeOrder2) wtobject;
				logger.debug(CLASSNAME + " ECN Number is  : "+ecnObject.getNumber());
				report = createReportHeader(report, ecnObject, headerList,
						headerList2);
			}
		}
		if(ecnObject!=null){
		resObjList = fetchAllResObj(ecnObject);
		logger.debug(CLASSNAME + " resObjList  : "+resObjList);
		//System.out.println("After fetchingresObjList");
		try {
			QueryResult activities = ChangeHelper2.service.getChangeActivities(
					ecnObject, true);
			while (activities.hasMoreElements()) {
				activity = (WTChangeActivity2) activities.nextElement();
				QueryResult queryResult = ChangeHelper2.service
						.getChangeablesAfter(activity);
				int size = queryResult.size();
				logger.debug(CLASSNAME + " getChangeablesAfter size  : "+size);
				for (int i = 0; i < size; i++) {
					wtObject = (wt.fc.WTObject) queryResult.nextElement();
					try {
						if (wtObject instanceof WTPart) {
							wtPart = (WTPart) wtObject;
							BOMexists = checkBOM.isBom(wtPart);
							logger.debug(CLASSNAME + "WTPART Number"+ wtPart.getNumber() + " | "+BOMexists);
							//System.out.println("BOMexists" + BOMexists);
							if (BOMexists) {
								report = createReportData(report, wtPart, null,
										activity, rowcnt, true, true,
										resObjList);
								rowcnt++;
								//System.out.println("Before GetChildren");
								rowcnt = getChildren(report, wtPart, activity,
										rowcnt, resObjList);
								//System.out.println("After GetChildren");
							} else {

								//System.out.println("inside else block");
								report = createReportData(report, wtPart, null,
										activity, rowcnt, false, true,
										resObjList);
								/*System.out.println("rowcnt before increment"
										+ rowcnt);*/
								rowcnt++;
								/*System.out.println("rowcnt after increment"
										+ rowcnt);*/
							}
						}
						if (wtObject instanceof EPMDocument
								|| wtObject instanceof WTDocument) {
							logger.debug(CLASSNAME + "Object is instance of EPMDocument or WTDocument");
							report = CarrierESIReportDetailsForEPM
									.createReportDataForEPMandWTDoc(report,
											wtObject, activity, rowcnt2);
							rowcnt2++;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}// try block ends
		catch (wt.util.WTException wtexcep) {
			wtexcep.printStackTrace();
		}
	}
		else{
				logger.debug(CLASSNAME + "Change Activity does not exist in Windchill");
				PrintWriter out = response.getWriter();
				response.setContentType("text/html"); 
				out.println("<script type=\"text/javascript\">");
	    	    out.println("alert('ECN Number does not exists! Please enter valid ECN number');");
	    	    out.println("location='/Windchill/wtcore/jsp/ext/carrier/wc/esi/CarrierESIValidation.jsp';");
	    	    out.println("</script>");
		}
		/**
		 * CarrierESIValidationReportAttach method is called to get the report
		 * path generated.
		 * 
		 */
		CarrierESIValidationReportAttach reportWriter = new CarrierESIValidationReportAttach();
		String reportPath = reportWriter.CarrierESIvalidationReportAttach(
				ecnObject.getNumber(), report);
		return reportPath;
	}

	/**
	 * @param workbook
	 * @param resultingPart
	 * @param usageLinkobj
	 * @param cActivity
	 * @param rowcnt
	 * @param isParent
	 * @param resObjPresent
	 * @return Method used for creating the Report Data
	 */
	public static HSSFWorkbook createReportData(HSSFWorkbook workbook,
			WTPart resultingPart, WTPartUsageLink usageLinkobj,
			WTChangeActivity2 cActivity, int rowcnt, boolean isParent,
			boolean resObjPresent, ArrayList<String> resPartList)
			throws WTException, IOException {
		//System.out.println("****createReportData********");
		String resObjNumber = "";
		String resObjName = "";
		String resObjSource = "";
		String resObjDefaultUnit = "";
		String resObjDrawingNum = "";
		String resAssDrawNum = "";
		String resObjPartQualifier = "";
		String resObjDesignCenter = "";
		String lineNumber = "";
		String findNumber = "";
		String resObjUnit = "";
		String resObjLc = "";
		String resParentNum = "";
		String resParentDT = "";
		String resQuantity = "";
		String resCurrentDT = "";
		String notAppl = "N/A";
		String mapDT = "";
		String resObjRev = "";
		String resObjIter = "";
		String LocDefault = "";
		String resLocation = "";
		String epmDrawNumList = "";
		String resContainer = "";
		String resUSCategory = "";
		String resUSClassifierEmail = "";
		String resUSDateClassified = "";
		String resUSECCN = "";
		String resUSJurisdiction = "";
		String resUSRationale = "";
		String resUSSource = "";
		List<String> currentEPMDraw = null;

		Set<WTPart> currentParentList = null;
		Set<String> mapParentDT = new HashSet<String>();

		if (isParent) {
			resParentNum = resultingPart.getNumber();
			resObjName = resultingPart.getName();
			LocDefault = resultingPart.getLocation();
			resContainer = resultingPart.getContainerName();
			resLocation = LocDefault.replace("Default", resContainer);
			resObjLc = resultingPart.getLifeCycleState().getDisplay();
			resObjRev = ((WTPart) resultingPart).getVersionIdentifier()
					.getValue();
			resObjIter = ((WTPart) resultingPart).getIterationIdentifier()
					.getValue();
			IBAHolder ibahldrPart = (IBAHolder) resultingPart;
			IBAReader compreader = new ext.carrier.wc.change.changenotice.IBAReader(
					ibahldrPart);
			resObjPartQualifier = (String) compreader
					.getValue("PART_QUALIFIER");

			resObjDrawingNum = (String) compreader.getValue("drawing_number");
			currentEPMDraw = getEPMDoc(resultingPart);
			if (currentEPMDraw.size() > 0) {
				epmDrawNumList = currentEPMDraw.toString();
				resAssDrawNum = epmDrawNumList.substring(1,
						epmDrawNumList.lastIndexOf("]"));
			}

			resObjDesignCenter = (String) compreader.getValue("designCenter");
			resUSCategory = (String) compreader.getValue("US_CATEGORY");
			resUSClassifierEmail = (String) compreader
					.getValue("US_CLASSIFIERS_EMAIL");
			resUSDateClassified = (String) compreader
					.getValue("US_DATE_CLASSIFIED");
			resUSECCN = (String) compreader.getValue("US_ECCN");
			resUSJurisdiction = (String) compreader.getValue("US_JURISDICTION");
			resUSRationale = (String) compreader.getValue("US_RATIONALE");
			resUSSource = (String) compreader.getValue("US_SOURCE");

			resObjSource = resultingPart.getSource().getDisplay();
			resObjDefaultUnit = resultingPart.getDefaultUnit().getDisplay();
			resCurrentDT = fetchDT(resultingPart);

		} else if (usageLinkobj != null) {
			resParentNum = usageLinkobj.getUsedBy().getNumber();

			if ((usageLinkobj.getLineNumber()) != null) {
				lineNumber = Long.toString(usageLinkobj.getLineNumber()
						.getValue());

			}
			if (usageLinkobj.getFindNumber() != null) {
				findNumber = usageLinkobj.getFindNumber();

			}
			if (Double.toString(usageLinkobj.getQuantity().getAmount()) != null) {

				resQuantity = Double.toString(usageLinkobj.getQuantity()
						.getAmount());

			}
			if ((usageLinkobj.getQuantity().getUnit()) != null) {
				resObjUnit = usageLinkobj.getQuantity().getUnit().getDisplay();

			}

			resObjNumber = resultingPart.getNumber();
			resObjName = resultingPart.getName();
			resObjLc = resultingPart.getLifeCycleState().getDisplay();
			LocDefault = resultingPart.getLocation();
			resContainer = resultingPart.getContainerName();
			resLocation = LocDefault.replace("Default", resContainer);

			resObjRev = ((WTPart) resultingPart).getVersionIdentifier()
					.getValue();
			resObjIter = ((WTPart) resultingPart).getIterationIdentifier()
					.getValue();
			IBAHolder ibahldrPart = (IBAHolder) resultingPart;
			IBAReader compreader = new ext.carrier.wc.change.changenotice.IBAReader(
					ibahldrPart);
			resObjPartQualifier = (String) compreader
					.getValue("PART_QUALIFIER");

			resObjDrawingNum = (String) compreader.getValue("drawing_number");
			currentEPMDraw = getEPMDoc(resultingPart);
			if (currentEPMDraw.size() > 0) {
				epmDrawNumList = currentEPMDraw.toString();
				resAssDrawNum = epmDrawNumList.substring(1,
						epmDrawNumList.lastIndexOf("]"));
			}

			resObjDesignCenter = (String) compreader.getValue("designCenter");
			resUSCategory = (String) compreader.getValue("US_CATEGORY");
			resUSClassifierEmail = (String) compreader
					.getValue("US_CLASSIFIERS_EMAIL");
			resUSDateClassified = (String) compreader
					.getValue("US_DATE_CLASSIFIED");
			resUSECCN = (String) compreader.getValue("US_ECCN");
			resUSJurisdiction = (String) compreader.getValue("US_JURISDICTION");
			resUSRationale = (String) compreader.getValue("US_RATIONALE");
			resUSSource = (String) compreader.getValue("US_SOURCE");
			resObjSource = resultingPart.getSource().getDisplay();
			resObjDefaultUnit = resultingPart.getDefaultUnit().getDisplay();
			resCurrentDT = fetchDT(resultingPart);
			resParentDT = fetchDT((WTPart) usageLinkobj.getUsedBy());

		} else {
			resParentNum = resultingPart.getNumber();
			resObjName = resultingPart.getName();
			LocDefault = resultingPart.getLocation();
			resContainer = resultingPart.getContainerName();
			resLocation = LocDefault.replace("Default", resContainer);
			resObjLc = resultingPart.getLifeCycleState().getDisplay();
			resObjRev = ((WTPart) resultingPart).getVersionIdentifier()
					.getValue();
			resObjIter = ((WTPart) resultingPart).getIterationIdentifier()
					.getValue();
			IBAHolder ibahldrPart = (IBAHolder) resultingPart;
			IBAReader compreader = new ext.carrier.wc.change.changenotice.IBAReader(
					ibahldrPart);
			resObjPartQualifier = (String) compreader
					.getValue("PART_QUALIFIER");

			resObjDrawingNum = (String) compreader.getValue("drawing_number");
			currentEPMDraw = getEPMDoc(resultingPart);
			if (currentEPMDraw.size() > 0) {
				epmDrawNumList = currentEPMDraw.toString();
				resAssDrawNum = epmDrawNumList.substring(1,
						epmDrawNumList.lastIndexOf("]"));
			}

			resObjDesignCenter = (String) compreader.getValue("designCenter");
			resUSCategory = (String) compreader.getValue("US_CATEGORY");
			resUSClassifierEmail = (String) compreader
					.getValue("US_CLASSIFIERS_EMAIL");
			resUSDateClassified = (String) compreader
					.getValue("US_DATE_CLASSIFIED");
			resUSECCN = (String) compreader.getValue("US_ECCN");
			resUSJurisdiction = (String) compreader.getValue("US_JURISDICTION");
			resUSRationale = (String) compreader.getValue("US_RATIONALE");
			resUSSource = (String) compreader.getValue("US_SOURCE");
			resObjSource = resultingPart.getSource().getDisplay();
			resObjDefaultUnit = resultingPart.getDefaultUnit().getDisplay();

			resCurrentDT = fetchDT(resultingPart);
			//System.out.println("****Start of parent details********");
			currentParentList = getParent(
					(WTPartMaster) resultingPart.getMaster(), resPartList);
			if (currentParentList.size() > 0) {
				for (WTPart parMapDetails : currentParentList) {
					mapDT = fetchDT(parMapDetails);
					if (mapDT != "") {
						mapParentDT.add(mapDT);
					}
				}
			}
			if (mapParentDT.size() > 0) {
				resParentDT = mapParentDT.toString();
				resParentDT = resParentDT.substring(1,
						resParentDT.lastIndexOf("]"));
			}
		}

		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = sheet.createRow(rowcnt);

		HSSFRow ecnDTValue = sheet.getRow(2);
		String ecnCellValue = ecnDTValue.getCell(1).toString();

		HSSFCell cell = row.createCell(0);
		cell.setCellValue(resParentNum);

		cell = row.createCell(1);
		if (resObjNumber != "") {
			cell.setCellValue(resObjNumber);
		} else {
			cell.setCellValue(notAppl);
		}
		cell = row.createCell(2);
		cell.setCellValue(resObjName);

		cell = row.createCell(3);
		cell.setCellValue(resObjPartQualifier);

		cell = row.createCell(4);
		cell.setCellValue(resObjRev + "." + resObjIter);

		cell = row.createCell(5);
		cell.setCellValue(resQuantity);

		cell = row.createCell(6);
		cell.setCellValue(resObjDefaultUnit);

		cell = row.createCell(7);
		cell.setCellValue(resObjSource);

		cell = row.createCell(8);
		if (lineNumber != "") {
			cell.setCellValue(lineNumber);
		} else {
			cell.setCellValue(notAppl);
		}

		cell = row.createCell(9);
		cell.setCellValue(resObjDrawingNum);
		cell = row.createCell(10);
		cell.setCellValue(resObjDesignCenter);
		cell = row.createCell(11);
		cell.setCellValue(cActivity.getNumber());
		cell = row.createCell(12);
		cell.setCellValue(cActivity.getLifeCycleState().getDisplay());
		cell = row.createCell(13);
		cell.setCellValue(cActivity.getName());
		cell = row.createCell(14);
		if (resParentDT != "") {
			cell.setCellValue(resParentDT);
		} else {
			cell.setCellValue("N/A");
		}
		cell = row.createCell(15);
		if (resCurrentDT != "") {
			cell.setCellValue(resCurrentDT);
		} else {
			cell.setCellValue("");
		}

		cell = row.createCell(16);

		if (resCurrentDT != "") {
			if (resCurrentDT.contains(ecnCellValue)) {
				cell.setCellValue(resCurrentDT);
			} else {
				cell.setCellValue(resCurrentDT + "," + ecnCellValue);
			}
		} else {
			cell.setCellValue(ecnCellValue);
		}

		cell = row.createCell(17);
		if (resObjPresent) {
			cell.setCellValue("YES");
		} else {
			cell.setCellValue("NO");
		}
		cell = row.createCell(18);
		cell.setCellValue(resObjLc);
		cell = row.createCell(19);
		cell.setCellValue(resObjUnit);
		cell = row.createCell(20);
		if (findNumber != "") {
			cell.setCellValue(findNumber);
		} else {
			cell.setCellValue(notAppl);
		}
		cell = row.createCell(21);
		cell.setCellValue(resLocation);
		cell = row.createCell(22);
		cell.setCellValue(resAssDrawNum);
		cell = row.createCell(23);
		cell.setCellValue(resUSCategory);
		cell = row.createCell(24);
		cell.setCellValue(resUSClassifierEmail);
		cell = row.createCell(25);
		cell.setCellValue(resUSDateClassified);
		cell = row.createCell(26);
		cell.setCellValue(resUSECCN);
		cell = row.createCell(27);
		cell.setCellValue(resUSJurisdiction);
		cell = row.createCell(28);
		cell.setCellValue(resUSRationale);
		cell = row.createCell(29);
		cell.setCellValue(resUSSource);

		return workbook;
	}

	/**
	 * @param workbook
	 * @param ecnObject
	 * @return HSSFWorkbook Method used for creating the Report Header details
	 * @Modified by Karthican V
	 */
	public static HSSFWorkbook createReportHeader(HSSFWorkbook workbook,
			WTChangeOrder2 ecnObject, ArrayList<String> headerList,
			ArrayList<String> headerList2) {
		//System.out.println("createReportHeader");
		String ecnDT = null;
		IBAHolder ibahldrPart = (IBAHolder) ecnObject;
		IBAReader compreader = new ext.carrier.wc.change.changenotice.IBAReader(
				ibahldrPart);
		ecnDT = (String) compreader.getValue("TargetDistribution");
		HSSFSheet sheet = workbook.createSheet(ecnObject.getNumber());
		sheet.setDefaultColumnWidth(25);
		HSSFSheet sheet2 = workbook.createSheet(ecnObject.getNumber()+"-2");
		sheet2.setDefaultColumnWidth(25);

		HSSFCellStyle cellStyle;
		HSSFFont font;
		font = workbook.createFont();
		font.setFontName("Courier New");
		font.setFontHeightInPoints(Short.parseShort("12"));
		//font.setBoldweight((short) 4);
		font.setBold(true); //ESR65193  WC11 CPS12
		font.setColor((short) 12);
		cellStyle = workbook.createCellStyle();
		cellStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		cellStyle.setFont(font);
		cellStyle.setWrapText(true);

		sheet = workbook.getSheetAt(0);
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN Number"));
		cell.setCellStyle(cellStyle);

		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnObject.getNumber()));

		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN Name"));
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnObject.getName()));

		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN DT"));
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnDT));

		row = sheet.createRow(3);
		cell = row.createCell(0);
		cell.setCellValue(new HSSFRichTextString("ECN State"));
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(new HSSFRichTextString(ecnObject.getState()
				.toString()));

		row = sheet.createRow(4);
		row = sheet.createRow(5);
		for (int col = 0; col < headerList.size(); col++) {
			cell = row.createCell(col);
			cell.setCellValue(new HSSFRichTextString(headerList.get(col)));
			cell.setCellStyle(cellStyle);
		}

		sheet2 = workbook.getSheetAt(1);
		HSSFRow row1 = sheet2.createRow(0);
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("ECN Number"));
		cell1.setCellStyle(cellStyle);
		cell1 = row1.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(ecnObject.getNumber()));

		row1 = sheet2.createRow(1);
		cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("ECN Name"));
		cell1.setCellStyle(cellStyle);
		cell1 = row1.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(ecnObject.getName()));

		row1 = sheet2.createRow(2);
		cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("ECN DT"));
		cell1.setCellStyle(cellStyle);
		cell1 = row1.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(ecnDT));

		row1 = sheet2.createRow(3);
		cell1 = row1.createCell(0);
		cell1.setCellValue(new HSSFRichTextString("ECN State"));
		cell1.setCellStyle(cellStyle);
		cell1 = row1.createCell(1);
		cell1.setCellValue(new HSSFRichTextString(ecnObject.getState()
				.toString()));

		row1 = sheet2.createRow(4);
		row1 = sheet2.createRow(5);

		for (int col1 = 0; col1 < headerList2.size(); col1++) {
			cell1 = row1.createCell(col1);
			cell1.setCellValue(new HSSFRichTextString(headerList2.get(col1)));
			cell1.setCellStyle(cellStyle);
		}

		return workbook;
	}

	/**
	 * @param part
	 *            Method to get the EPMDoc drawing number
	 * @return List
	 * 
	 */

	public static List<String> getEPMDoc(WTPart part)
			throws WTRuntimeException, WTException, IOException {
		//System.out.println("inside drawing document details");
		List<String> drawingList = new ArrayList<String>();
		WTSet epmdocs = EPMNavigateHelper.navigate(part,
				UIRelationships.newAssociatedCADDocs(), CollectItem.OTHERSIDE)
				.getResults(new WTHashSet());
		EPMDocument releatedEPMDoc = null;
		for (Iterator it = epmdocs.persistableIterator(); it.hasNext();) {
			Object nextObj = it.next();
			if (nextObj instanceof EPMDocument) {
				releatedEPMDoc = (EPMDocument) nextObj;

				if (releatedEPMDoc.getDocType().equals(
						EPMDocumentType.toEPMDocumentType("CADDRAWING"))) {
					drawingList.add(releatedEPMDoc.getNumber());
					/*System.out.println("the drawing document number"
							+ releatedEPMDoc.getNumber());*/
				}
			}
		}
		logger.debug(CLASSNAME + "drawingList : "+drawingList);
		return drawingList;
	}

	/**
	 * @param part
	 *            Method to get the parent details if present
	 * @return arraylist
	 * 
	 */

	public static Set<WTPart> getParent(WTPartMaster part,
			ArrayList<String> resObjList) {

		logger.debug(CLASSNAME + "****inside getParent***");
		Set<WTPart> parentList = new HashSet<WTPart>();
		HashMap<String, WTPart> resList = new HashMap<String, WTPart>();

		try {
			wt.fc.QueryResult result = wt.part.WTPartHelper.service
					.getUsedByWTParts(part);
			logger.debug(CLASSNAME + "****QueryResult size***" + result.size());
			while (result.hasMoreElements()) {
				WTPart wtUsedByPart = (WTPart) result.nextElement();
				logger.debug(CLASSNAME + "****Used By Part Number***"
						+ wtUsedByPart.getNumber()+ " | "+ wtUsedByPart.getVersionIdentifier().getValue());
				if (resObjList.contains(wtUsedByPart.getNumber())) {
					resList.put(wtUsedByPart.getNumber(), wtUsedByPart);
				}
			}
			logger.debug(CLASSNAME + " ***resList.size()****" + resList.size());
			if (resList.size() > 0) {
				for (String parMapDetails : resList.keySet()) {
					WTPart wtParentList = (WTPart) resList.get(parMapDetails);
					QueryResult queryresult = ConfigHelper.service
							.filteredIterationsOf(
									(WTPartMaster) wtParentList.getMaster(),
									new LatestConfigSpec());
					logger.debug(CLASSNAME + "****checking for parent details***"
							+ queryresult.size());
					while (queryresult.hasMoreElements()) {
						WTPart wtparent = (WTPart) queryresult.nextElement();
						logger.debug(CLASSNAME + "****Parent Number***"
								+ wtparent.getNumber());
						logger.debug(CLASSNAME + "****Version***"
								+ wtparent.getVersionIdentifier().getValue());
						logger.debug(CLASSNAME + "****Iteration***"
								+ wtparent.getIterationIdentifier().getValue());
						parentList.add(wtparent);

					}
				}
				logger.debug(CLASSNAME + "****Set Iteration List Size***"
						+ parentList.size());
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return parentList;
	}

	/**
	 * @param part
	 * @param activity
	 * @param rowcnt
	 * @param resObjList
	 *            Method used for fetching the UsageLink Objects.
	 * @return rowcnt
	 * 
	 */

	public static int getChildren(HSSFWorkbook report, WTPart part,
			WTChangeActivity2 activity, int rowcnt, ArrayList<String> resObjList)
			throws IOException {
		logger.debug(CLASSNAME + " Inside getChildren method ");
		boolean checkResObj = false;
		try {
			wt.fc.QueryResult result = wt.part.WTPartHelper.service
					.getUsesWTPartMasters(part);
			logger.debug(CLASSNAME + "****getChildren size***" + result.size());
			while (result.hasMoreElements()) {
				WTPartUsageLink wtPartUsageLinkObj = (WTPartUsageLink) result
						.nextElement();
				WTPartMaster wtpartmaster = (WTPartMaster) (wtPartUsageLinkObj)
						.getUses();
				QueryResult queryresult = ConfigHelper.service
						.filteredIterationsOf(wtpartmaster,
								new LatestConfigSpec());
				logger.debug(CLASSNAME + " queryresult size :  " +queryresult.size());
				while (queryresult.hasMoreElements()) {
					WTPart wtpart1 = (WTPart) queryresult.nextElement();
					logger.debug(CLASSNAME + " wtpart1 :  " +wtpart1.getNumber());
					if (resObjList.contains(wtpart1.getNumber()))
						checkResObj = true;
					else
						checkResObj = false;
					report = createReportData(report, wtpart1,
							wtPartUsageLinkObj, activity, rowcnt, false,
							checkResObj, resObjList);
					rowcnt++;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		//System.out.println("rowcnt" + rowcnt);
		return rowcnt;
	}

	/**
	 * @param WTChangeOrder2
	 *            Method used for fetching the resulting objects checks whether
	 *            the child part is present in the resulting object.
	 * @return ArrayList
	 * 
	 */

	public static ArrayList<String> fetchAllResObj(WTChangeOrder2 ecnObject) {
		ArrayList<String> fetchResObjList = new ArrayList<String>();
		WTChangeActivity2 activity = null;
		try {
			QueryResult activities = ChangeHelper2.service.getChangeActivities(
					ecnObject, true);

			while (activities.hasMoreElements()) {
				activity = (WTChangeActivity2) activities.nextElement();
				QueryResult queryResult = ChangeHelper2.service
						.getChangeablesAfter(activity);
				logger.debug(CLASSNAME + " queryresult size :  " +queryResult.size());
				wt.fc.WTObject wtObject = null;
				WTPart wtPart = null;
				while (queryResult.hasMoreElements()) {
					wtObject = (wt.fc.WTObject) queryResult.nextElement();
					try {
						if (wtObject instanceof WTPart) {
							wtPart = (WTPart) wtObject;
							fetchResObjList.add(wtPart.getNumber());
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}
			fetchResObjList.trimToSize();
			logger.debug(CLASSNAME + " fetchResObjList size :  " +fetchResObjList.size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fetchResObjList;
	}

	/**
	 * @param part
	 *            Method used for fetching the Distribution Target
	 * @return string
	 * 
	 */

	public static String fetchDT(WTPart part) {
		ESITarget distributionTarget = null;
		String targetNumber = "";
		Collection targetAssignmentsNew;
		try {
			targetAssignmentsNew = ESIHelper.service.findESITargets(part);
			Iterator iteratorNew1 = targetAssignmentsNew.iterator();
			while (iteratorNew1.hasNext()) {
				Object getDTObj = iteratorNew1.next();
				if (getDTObj instanceof ESITarget) {
					distributionTarget = (ESITarget) getDTObj;
				} else if (getDTObj instanceof ESITargetAssignmentLink) {
					distributionTarget = ((ESITargetAssignmentLink) getDTObj)
							.getTarget();
				}
				if (targetNumber.equals(""))
					targetNumber = distributionTarget.getNumber();
				else
					targetNumber = targetNumber + ","
							+ distributionTarget.getNumber();
			}

		} catch (ESIException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		logger.debug(CLASSNAME + "targetNumber value====>" + targetNumber);
		return targetNumber;
	}
}

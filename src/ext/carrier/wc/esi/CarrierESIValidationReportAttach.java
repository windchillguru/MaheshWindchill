/**
 *** Purpose:  This java file used for creating the report in the temp folder.
 **** Implemented for ESR# 18726
 *** History: 	Created on 22-Dec-2015
 *** Author: Varsha P.L
 */

package ext.carrier.wc.esi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTProperties;

public class CarrierESIValidationReportAttach {
	private static String wthome = null;
	public static String strTempPathForReportFile = null;
	public static Properties properties = null;
	public static Properties properties2 = null;
	public static java.util.Locale locale = java.util.Locale.getDefault();
	protected static File file;
	static {
		try {
			properties = ServiceProperties
					.getServiceProperties("WTServiceProviderFromProperties");
			properties2 = WTProperties.getLocalProperties();
			wthome = properties2.getProperty("wt.home");
			strTempPathForReportFile = properties
					.getProperty("ext.carrier.wc.esi.strTempPathForESIReportFile");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This method Creates ECN report in the temp location with Report name in
	 * the format ECNNumber_Timestamp.xls
	 * 
	 * @param ECNNumber
	 *            - used to formulate the Report file name
	 * @param report
	 *            - workbook
	 **/
	public String CarrierESIvalidationReportAttach(String ECNNumber,
			HSSFWorkbook report) throws Exception {

		String logFileName = wthome + File.separator + strTempPathForReportFile
				+ File.separator + ECNNumber + "_"
				+ getCurrentTime("yyyyMMddHHmmss") + ".xls";

		file = new File(logFileName);
		if (!file.exists()) {
			boolean flag = false;
			try {
				System.out.println("****flag inside block***");
				flag = file.createNewFile();
			} catch (IOException ioe) {
				String s = file.getParent();
				if (s != null)
					flag = new File(s).mkdirs();
			}
			if (!flag) {
				throw new Exception("Could not create report file.");
			}
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		try {

			report.write(fileOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		return logFileName;
	}

	/**
	 * This method gets current timestamp of America/New_York
	 * 
	 * @param dateFormat
	 * @return String- time
	 */
	public String getCurrentTime(String dateFormat) {
		java.util.Calendar calender = java.util.Calendar.getInstance();
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
				dateFormat);
		format.setTimeZone(java.util.TimeZone.getTimeZone("America/New_York"));
		return format.format(calender.getTime());
	}

}
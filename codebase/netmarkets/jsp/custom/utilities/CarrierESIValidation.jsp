<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.StringTokenizer"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.services.applicationcontext.implementation.ServiceProperties"%>
<%@page import="java.util.*" %>
<%@page import="java.io.*"%>
<%@page import="ext.carrier.wc.esi.CarrierESIValidationReport" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Carrier ESI Validation Report</title>
</head>
<body>
	<form name="myForm" method="" action="">	
	<font face="verdana,arial" size=-1>
	<center>
		<table cellpadding=2 cellspacing=0 border=0 width=30%>
			<tr>
				<th bgcolor="blue" align=center style="padding:2;padding-bottom:4">
				<b><font size=-1 color="white">Enter ECN Number</font></th>
			</tr>
			<tr>
				<table cellpadding=2 cellspacing=0 border=0 width=30%>
					<tr>
						<td><font face="verdana,arial" size=-1>ECN Number</td>
						<td><input type="text" name="number" size=20></td>
					</tr>
					<tr>
						<table cellpadding=2 cellspacing=0 border=0 width=30%>
						<tr>
							<td><font face="verdana,arial" size=-1>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td><font face="verdana,arial" size=-1><input type="submit" value="Submit"></td>
							<td><font face="verdana,arial" size=-1><input type="reset" value="Clear"></td>
						</tr>
						</table>
					</tr>
				</table>
			</tr>
		</table>
	</center>
	</form>
<%
InputStream in = null;
int ch;
String strEcnNumber= request.getParameter("number");
System.out.println("****strEcnNumber****"+strEcnNumber);
if ((strEcnNumber != null) && (!strEcnNumber.equals(""))){
		String ESIReportPath=ext.carrier.wc.esi.CarrierESIValidationReport.checkECNNumber(strEcnNumber,response);
		String ESIreportName=ESIReportPath.substring(33);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition","attachment;filename=\""+ESIreportName+"\"");
		File fileToDownload = new File(ESIReportPath);
		ServletOutputStream outs = response.getOutputStream();
		System.out.println("****ESIreportName****"+outs);
	try {
		 in = new BufferedInputStream(new FileInputStream(fileToDownload));
			
			while ((ch = in.read()) != -1) {
				 outs.print((char) ch);
				}
		 } // end try, getting the ECN
		catch(Exception e)
		{ // begin catch, in case of any exception forward to error page
			System.out.println("****Exception****");	}
			finally {
				if (in != null) in.close(); // very important
				}
				outs.flush();
				outs.close();
				in.close();
	}	
%>	
</body>
</html>



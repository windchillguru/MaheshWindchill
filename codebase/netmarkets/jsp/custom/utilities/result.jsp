<%@page import="com.ptc.netmarkets.model.NmOid,
                 com.ptc.netmarkets.object.objectResource,
                 com.ptc.netmarkets.project.NmProject,
                 com.ptc.netmarkets.project.NmProjectCommands,
                 com.ptc.netmarkets.project.projectResource,
                 java.util.ResourceBundle"
%>  
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<%@page import="java.beans.PropertyVetoException"%>  
<%@page import="wt.util.WTException"%>  
<%@page import="wt.util.WTInvalidParameterException"%>  
<%@page import="java.io.*"%>  
<%@page import="java.util.ArrayList"%>  
<%@page import="java.util.HashMap"%>  
<%@page import="java.util.*"%> 
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.fc.WTObject"%>

<%@ page import="ext.com.lnties.mahesh.utilities.DownloadSecondaryContent"%>

<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request" />
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>


	
	<% DownloadSecondaryContent obj = new DownloadSecondaryContent(); %>
	
	<%
		String filePath = null;
		try{
			out.println("Inside the jsp file newUpdated - commandBean:: "+commandBean);
			NmOid oid = commandBean.getPrimaryOid();
			out.println("### NmOid oid:: "+oid +"------");  		
			filePath=obj.downloadFolderContentFiles(commandBean);
			out.println("####-- In Jsp File filePath is:: "+filePath);
			System.out.println("####-- In Jsp File filePath is:: "+filePath);
		}
		 catch (Exception e) {
			e.printStackTrace();
		}			
		if(filePath == null || "".equals(filePath)) {
	%>	
		<b>Error Occurred While downloading Attachment</b><BR>		
	<%
		} else {
	%>
		<script language="javascript">
		       var fileURL = "<%=filePath%>" ;
			   alert("fileURL is:: "+fileURL);
			   if(fileURL){
				   window.opener.PTC.util.downloadUrl(fileURL);
				   window.close() ;
				   }
		</script>
		   
	 <%    
		}
	%> 

	
</body>
</html>





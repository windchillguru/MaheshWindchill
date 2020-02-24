<%@ page import="com.ptc.netmarkets.model.NmOid,
                 com.ptc.netmarkets.object.objectResource,
                 com.ptc.netmarkets.project.NmProject,
                 com.ptc.netmarkets.project.NmProjectCommands,
                 com.ptc.netmarkets.project.projectResource,
                 java.util.ResourceBundle"
%>

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request" />

<%!
   private static final String PROJECT_RESOURCE = "com.ptc.netmarkets.project.projectResource";
   private static final String OBJECT_RESOURCE = "com.ptc.netmarkets.object.objectResource";
   private static final String DOCUMENT_RESOURCE = "com.ptc.windchill.enterprise.doc.documentResource";
%>

<%



   ResourceBundle projectRb = ResourceBundle.getBundle(PROJECT_RESOURCE, localeBean.getLocale());
   ResourceBundle objectRb = ResourceBundle.getBundle(OBJECT_RESOURCE, localeBean.getLocale());
   ResourceBundle documentRb = ResourceBundle.getBundle(DOCUMENT_RESOURCE, localeBean.getLocale());

   NmOid oid = commandBean.getPrimaryOid();
   String exportFileLabelBlurb = "";
   String containerName="";
   String containerClassName=""; 
   
   if (oid !=null && oid.getContainerReference()!=null){
               
    containerName=commandBean.getViewingContainer().getName();
         
    containerClassName =oid.getContainerReference().getKey().getClassname();
   
   
   if("wt.pdmlink.PDMLinkProduct".equals(containerClassName)){
         
         exportFileLabelBlurb = documentRb.getString(documentResource.PRODUCT_EXPORT_FILE_LABEL);  
         
   }else if("wt.inf.library.WTLibrary".equals(containerClassName)){
         
         exportFileLabelBlurb = documentRb.getString(documentResource.LIBRARY_EXPORT_FILE_LABEL);  
         
         
         
   }else if("wt.inf.container.OrgContainer".equals(containerClassName)){
         
         exportFileLabelBlurb = documentRb.getString(documentResource.ORG_EXPORT_FILE_LABEL);
         
         
   }else if("wt.inf.container.ExchangeContainer".equals(containerClassName)){
         
         
         exportFileLabelBlurb = documentRb.getString(documentResource.SITE_EXPORT_FILE_LABEL);
         
         
   }else if("wt.projmgmt.admin.Project2".equals(containerClassName)){
   boolean isProgram = false;      
   try{
	   isProgram = NmProjectCommands.isProgram(commandBean);
   }catch(Exception ex){
	   
   }
   
   if( isProgram) {
    exportFileLabelBlurb = documentRb.getString(documentResource.PROGRAM_EXPORT_FILE_LABEL);
   } else {
    exportFileLabelBlurb = documentRb.getString(documentResource.PROJECT_EXPORT_FILE_LABEL);
   }
   }      
         
   } 
   
%>



<table border="0">
   <tr valign="top">
      <td align="left" valign="middle">
         <font class=wizardlabel>
            Click OK to Download Secondry Dovcument
         </font>
      </td>
   </tr>
</table>
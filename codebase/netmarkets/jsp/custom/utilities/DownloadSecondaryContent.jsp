<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<fmt:setBundle basename="com.ptc.netmarkets.object.objectResource"/>
<fmt:message var="title" key="DOWNLOAD_ARCHIVE_TITLE" />

<jca:wizard buttonList="NoStepsWizardButtons" title="${title}" helpSelectorKey="DocMgmtDocDownload">
   <jca:wizardStep action="downloadSecondaryContentStep" type="document"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
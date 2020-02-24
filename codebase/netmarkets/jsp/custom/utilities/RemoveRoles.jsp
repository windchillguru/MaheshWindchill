<%@page import="ext.com.lnties.mahesh.Users.UserDeactivation"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%@ page import="wt.org.WTUser"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>


<body>

<%
String userName = request.getParameter("userID");

System.out.println("Inside remove roles ### user is -->"+userName);

String selectedRoles[] = request.getParameterValues("roleListVal"); 
if (selectedRoles != null && selectedRoles.length != 0) {
out.println("You have selected: ");
System.out.println("You have selected: ");

String test[] =null;

for (String role : selectedRoles){
	System.out.println("You have selected: ##Logger ## " + role);
	
	test = role.split("-->");
	
	/* for(int i=0; i< test.size(); i++){
		System.out.println("test container is### "+test[0]);
		System.out.println("test role is####" + test[1].trim()+"###");
	} */
	
	
	
	
	//System.out.println("index of - ###"+test[0].indexOf("-", 0));
	
	// String container[] = test[0].split(" - ");
	
	
	// imprtatnt final line for container name
	//String containerNAME = test[0].substring(test[0].indexOf("-", 0)+2, test[0].length()-1);
	
	// System.out.println("container name ####:::::##"+containerNAME.trim()+"####");
	
	
	String containerNAME = test[0].trim();
	String roleNAME = test[1].trim();
	System.out.println("container ######"+containerNAME+"####");
	
	
	
	System.out.println("role name ######"+roleNAME+"####");
	
	WTUser wtUser = UserDeactivation.getUserByname(userName); // fetching user here
	
	 UserDeactivation.removeUserFromRole(wtUser, containerNAME, roleNAME);
	
	// need to change method signate of removeUserFromRole() method
	
}




/* for (int i = 0; i < selectedRoles.length; i++) {
out.println(selectedRoles[i]);
	System.out.println(selectedRoles[i]);
	
	
} */



}



	// request.setAttribute("userID", userName);
	 
	
	//response.sendRedirect("userDetails.jsp");
	
	
	

%>

<script>

alert("Removed users from selected roles...");
</script>

<form id="redirectform" action="userDetails.jsp" method="post">
<input type="hidden" value="<%=userName%>" name="userID">

</form>

<script type="text/javascript">

document.getElementById("redirectform").submit();
</script>

<%
System.out.println("resubmitted");
%>



</body>
</html>
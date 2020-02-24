<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	<%@ page import = "ext.com.lnties.mahesh.Users.UserDeactivation"%>
	<%@ page import="wt.org.WTUser"%>
	<%@page import="ext.com.lnties.mahesh.Users.UserDetails"%>
	<%@ page import="java.util.*"%> 
	<%@ page import="wt.org.WTUser"%>
	<%@page import="ext.com.lnties.mahesh.Users.UserDeactivation"%>
	
	
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">

//JavaScript function that enables or disables a submit button depending
//on whether a checkbox has been ticked or not.
function checkboxValidator(termsCheckBox){
  //If the checkbox has been checked
  if(termsCheckBox.checked){
      //Set the disabled property to FALSE and enable the button.
      document.getElementById("removeRoleBtn").disabled = false;
  } else{
      //Otherwise, disable the submit button.
      document.getElementById("removeRoleBtn").disabled = true;
  }
}



</script>


<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Local User Details</title>
</head>
<body onload="checkboxValidate()">
	
	<div>
	<form name="myForm" method="post" action="userDetails.jsp" >
	
	
	<font face="verdana,arial" size=-1>
		
		<center>
		
		
		<table cellpadding='2' cellspacing='0' border='0' width='30%'>
		<tr>
			<th bgcolor="blue" align=center style="padding:2;padding-bottom:4">
			<b><font size=-1 color="white">Enter user id</font></b></th>
		</tr>
		
		
		<%
		
		String userID= (String)request.getParameter("userID");
		
		System.out.println("#########userID : " +userID);
		
		List<String> groupsList = new ArrayList<String>();
		
		List<String> containerRolesList = new ArrayList<String>();
		
		List<String> tasksList = new ArrayList<String>();
		
		boolean userAvailable = false;
		
		// BigDecimal userIdA2A2 = null;
		boolean temp = false;
		if(userID != null &&  userID != ""){
			
			
			
			WTUser wtUser = UserDeactivation.getUserByname(userID); // fetching user here
			
				if(wtUser != null){
					userAvailable = true;
					
					System.out.println("######### IDA2a2 : " +wtUser.getPersistInfo().getObjectIdentifier().getId());
					
					groupsList = UserDeactivation.getAllUserGroups(wtUser);
					
					
			
					containerRolesList = UserDeactivation.getUserRoles(wtUser);
					
					
					tasksList = UserDeactivation.getAllTasksOfUser(wtUser);
					
			
				%>
				
				<table border="1" style="width:80%;">
				
				 
				<tr>
					<th rowspan="1" style="width:20%;">User Name</th>
					<td><%=userID%> </td> 
				</tr><tr></tr>
				
				
				<!-- group section -->
				
				<tr>
				
				<th rowspan="<%=groupsList.size()%>" style="width:20%;">User Group Name(s)</th>
				
				<%
				if(groupsList != null){
					
					for (String groupName : groupsList) {
						System.out.println("## Logger ## " +groupName);
					
						%>
						<td><%=groupName%> </td> </tr><tr>
						
						<%
						
					}
					
					
					
				}%>
				
				
				</tr><tr></tr>
				
				<!-- role section -->
				
				<%-- <tr>
				
				<th rowspan="<%=containerRolesList.size()%>" style="width:20%;"> User Roles</th>
				<!-- <form name="rolesCheckList"  action="RemoveRoles.jsp" method="post">  -->
				<%
				if(containerRolesList != null){
					Iterator<?> iterator = containerRolesList.iterator();
					
					while (iterator.hasNext()){
						%>
						
						<td><input type="checkbox" name="role" value="<%=iterator.next().toString()%>"><%=iterator.next().toString()%> </td> </tr><tr>
						
						<%
						
					}
					
					
					
				}%>
				
				
				<input type="hidden" value="<%=userID%>" name="userID">
				<input type="submit" value="Deactivate"/>
				<!-- </form>   -->
				
				</tr><tr></tr> --%>
				
				<!-- tasks section -->
				
				<tr>
				
				<th rowspan="<%=tasksList.size()%>" style="width:20%;">User Tasks</th>
				
				<%
				if(tasksList != null){
					
					
					
					for (String task : tasksList) {
						System.out.println("## Logger ## " + task);
					
					
						%>
						<td><%=task%> </td> </tr><tr>
						
						<%
						
					}
					
					
					
				}%>
				
				
				</tr><tr></tr>
				
				
			
				
								
				<!-- </table> -->
				
		
		<%
		
		
			
		
		
				}else {
					userAvailable=false;
					//check user exist in WIndchill or not here
			%>
				<tr>
						<td colspan=2><font face="verdana,arial" size=-1>
					<b>Windchill ID does not exist for email <%=userID%><b><br>
					If you have entered an incorrect email, <a href="/Windchill/netmarkets/jsp/custom/utilities/userDetails.jsp"> please try again </a>.<br>
					Otherwise send an email to <a href="mailto:esr@carrier.utc.com?subject=Windchill ID Creation Request">esr@carrier.utc.com</a> for assistance.</td>
				</tr><%
				}
		}
		%>
		
		
		
		
		<% 
		if(userID==null || userID==""){ %>
			
			
			
			
		
		<table>
		
		
		<tr>
		
		<td><font face="verdana,arial" size=-1>Windchill User ID:</font></td>
					<td><input type="text" name="userID" size=30></td>
					
		
		</tr>
		<tr>
						<td><font face="verdana,arial" size=-1><input type="checkbox" name="debug" value="Debug">Debug</font></td>
					</tr>
		
		<tr>
		
			<td>
			
			<font face="verdana,arial" size=-1>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>
			</td>
			
			<td><font face="verdana,arial" size=-1><input type="submit" value="Submit"></font></td>
			<td><font face="verdana,arial" size=-1><input type="reset" value="Clear"></font></td>
			
		
		</tr>
		</table><%
		
}
		
		%>
		
		
		<!-- </table> -->
		
		</center>
		
		</font>
	</form>
	<%
		if(userAvailable){
			System.out.println("Inside if user availabe ### user is -->"+userID);

		%>
			
			<form name="rolesCheckList"  action="RemoveRoles.jsp" method="post" id="rolesForm" onsubmit="return confirm('Are you sure you want to remove this user from selected roles?');">
			
			<tr>
				
				<th rowspan="<%=containerRolesList.size()%>" style="width:20%;"> User Roles</th>
			<%
				if(containerRolesList != null){
					
					
					for (String containerRole : containerRolesList) {
						System.out.println("## Logger ## " +containerRole);
					
					
					
						%>
						
						<td><input type="checkbox" name="roleListVal" value="<%=containerRole%>"
						onclick="checkboxValidator(this)" ><%=containerRole%> </td> </tr><tr>
						
						<%
						
					}
					
					
					
				}%>
				
				
			
				
				
				</tr><tr></tr>
				<input type="hidden" value="<%=userID%>" name="userID">
				
				</table>
				<br/>
			
				<input type="submit" value="Remove from roles" id="removeRoleBtn" disabled />
				<input  type="reset" value="Clear Checks" onclick="checkboxValidator(this)"/>
				 </form>   
				 </table>
				 
				 
			
		<%}
	
	/* Object username = request.getAttribute("userID");
	
	System.out.println("incoming data after user roles process ## " + username.toString()); */
	
	
	%>
	
	
	
	
	</div>
</body>
</html>
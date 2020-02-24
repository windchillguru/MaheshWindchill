<%@ page import="java.util.ArrayList,
				java.util.Scanner,
				java.io.FileReader,
				java.util.Map,
				java.io.File,
				java.net.InetAddress" %>
<%
		Map localMap = System.getenv();
		//String str1 = "WT_HOME";
		//String fileLoc=(String)localMap.get(str1) + File.separator + "custmUtilList" + File.separator +"UtilNames_TabSep.txt";
		
		String fileLoc = "E:\\PTC\\Windchill_11.0\\Windchill\\codebase\\netmarkets\\jsp\\custom\\utilities\\UtilNames_TabSep.txt";
		
		String navigateURSPath = "E:\\PTC\\Windchill_11.0\\Windchill\\codebase\\netmarkets\\jsp\\custom\\utilities\\Navigate_URLS.txt";
		Scanner scan;
		Scanner scan_navigate;
		try {
			scan = new Scanner(new FileReader(fileLoc));
	
		scan_navigate = new Scanner(new FileReader(navigateURSPath));
		
		
	    ArrayList<String> hyperlink = new ArrayList<String>();
	    ArrayList<String> displayName = new ArrayList<String>();
		 ArrayList<String> hyperlinkDesc= new ArrayList<String>();

		 ArrayList<String> hyperlink_Navigate = new ArrayList<String>();
	    ArrayList<String> displayName_Navigate = new ArrayList<String>();
		 ArrayList<String> hyperlinkDesc_Navigate = new ArrayList<String>();
		
	    while(scan.hasNext()){
	        String curLine = scan.nextLine();
	        String[] splitted = curLine.split("\t");
	        String link = splitted[0].trim();
	        String name = splitted[1].trim();
			String linkDesc = splitted[2].trim();
	        if(!"Link".equals(link)){
	            hyperlink.add(link);
	        }
	        if(!"Name".equals(name)){
	            displayName.add(name);
	        }
			if(!"Desc".equals(linkDesc)){
	            hyperlinkDesc.add(linkDesc);
	        }
	    }
		
		// navigate processing
		
		while(scan_navigate.hasNext()){
	        String curLine = scan_navigate.nextLine();
	        String[] splitted = curLine.split("\t");
	        String link = splitted[0].trim();
	        String name = splitted[1].trim();
			String linkDesc = splitted[2].trim();
	        if(!"Link".equals(link)){
	            hyperlink_Navigate.add(link);
	        }
	        if(!"Name".equals(name)){
	            displayName_Navigate.add(name);
	        }
			if(!"Desc".equals(linkDesc)){
	            hyperlinkDesc_Navigate.add(linkDesc);
	        }
	    }
		
%>


<HTML>
<HEAD><TITLE>Custom Utilities Page</TITLE>
<LINK REL=stylesheet HREF="netmarkets/css/windchill-base.css" TYPE="text/css">
<LINK REL=stylesheet HREF="netmarkets/javascript/ext/resources/css/ext-sandbox.css" TYPE="text/css">
<LINK REL=stylesheet id="theme0" HREF="netmarkets/themes/windchill/xtheme-windchill.css" TYPE="text/css">

<style>
body {background-color:rgba(0, 0, 255, 0.1);}
a{color:blue;font-family:Courier}
td{font-family:Courier}
th{font-family:Courier}
</style>
</HEAD>
<BODY>
<center>
<br>

		<table cellpadding=1 cellspacing=0 border=1 width=90% >
		<tr><th bgcolor="blue" align=center style="padding:10;padding-bottom:4">
		<b><font size=3 color="white">Utility</font></th>
		<th bgcolor="blue" align=center style="padding:10;padding-bottom:4">
		<b><font size=3 color="white">Description</font></th>
		</tr>
		
		<%for(int i=0;i<hyperlink.size();i++){ %>
		<tr><td align="center" style="padding:2;padding-bottom:2">
		<font face="verdana,arial" size=1>
		<a href='<%=hyperlink.get(i)%>' target="_blank"><b><%=displayName.get(i)%></b></a>
		</td> 
		<td align="center" style="padding:8;padding-bottom:2">
		<font face="verdana" size=1>
		<%=hyperlinkDesc.get(i)%>
		</td> </tr> 
		
		
		
		<%scan.close(); 
			
					}%>Navigate URLS :
		<%
		
		
		String HOST_NAME = "windchillstage";
		int j=0;
		
		if(HOST_NAME.equals("windchill")){
			j = 0;
		}else if(HOST_NAME.equals("windchilldev")){
			j = 1;
		}else if(HOST_NAME.equals("windchillstage")){
			j = 2;
		}			
		
		
		%>
		<tr><td align="center" style="padding:2;padding-bottom:2">
		<font face="verdana,arial" size=1>
		
		
		<a href='<%=hyperlink_Navigate.get(j)%>' target="_blank"><b><%=displayName_Navigate.get(j)%></b></a>
		</td> 
		<td align="center" style="padding:8;padding-bottom:2">
		<font face="verdana" size=1>
		<%=hyperlinkDesc_Navigate.get(j)%>
		</td> </tr> <%
					
				scan_navigate.close();	
					
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} %>
		
		</table>
		<% InetAddress ia = InetAddress.getLocalHost(); %>
Hostname:<%=ia.getHostName()%>
Local Hostname:<%=ia.getLocalHost()%>
		<p>The context path is: ${pageContext.request.contextPath}.</p>

</center>
</BODY>
</HTML>

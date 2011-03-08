<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="singer.iReceipt" %>
<%@ page import="singer.PMF" %>
<%@ page import="google.proj.IDate" %>
<%@ page import="singer.Statics" %>
<%@ page import="java.lang.Integer"%>


<html>
<head>
<%PersistenceManager pm = PMF.get().getPersistenceManager();
Statics statics=null;
UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
	if (user!=null){
	
statics=Statics.loadStatics(pm,user.getEmail());}%>

    <link type="text/css" rel="stylesheet" href="stylesheets/secondary_style.css" />
    <SCRIPT LANGUAGE="JavaScript" SRC="/CalendarPopup.js"></SCRIPT>
	<SCRIPT LANGUAGE="JavaScript">
	var cal = new CalendarPopup();
	</SCRIPT>
    
    <script language="javascript">
		function checkform()
	{
		if (document.personal.business.value==""){
			alert("Business name can not be left blank.")
			return false;
		}
		if (document.personal.total.value==""){
			alert("Total can not be left blank.")
		return false;
	    }
		if (document.personal.date.value==""){
			 alert("Date can not be left blank.")
			 return false;
		}


	return true;
}

</script>

<script type="text/javascript">
function testNumber(field,original_text) {
    var regExpr = new RegExp("^\\d*\\.?\\d*$");
    if (!regExpr.test(field.value)) {
      field.value = original_text;
    }
}

</script>

<script type="text/javascript">
function enable(){
if (document.personal.imageupdate.checked==true) 
	document.personal.file.disabled=false;
else
	document.personal.file.disabled=true;
} 
</script> 




<script src="mootools.js" type="text/javascript"></script>

<link rel="stylesheet" href="sexyalertbox.css" type="text/css" media="all" />

<script src="sexyalertbox.packed.js" type="text/javascript"></script>




<% if (statics!=null){%>
<script type="text/javascript">

window.addEvent('domready', function() {

    Sexy = new SexyAlertBox();
	test();

});

function test() {
	<%int num=statics.getNumber_of_alerts();
	if (num>0){
		boolean boo=false;
		Double current = 0.0;
		for(int t = 1;t<=num;t++){
			String time = statics.getAlertTime(t);
			Double limit= statics.getAlertlimit(t);
			String text = statics.getAlertText(t);
			current=statics.getMonthlyxpenses();
			if (time.equals("Monthly"))
				if(statics.getMonthlyxpenses()>=limit)
					boo=true;
				
			
			if (time.equals("Yearly"))
			  if(statics.getYearlyxpenses()>=limit)
					boo=true;
			
			if (time.equals("Total"))
			  if(statics.getTotalexpenses()>=limit)
					boo=true;
			
			if (boo==true){
				out.write("Sexy.alert('<h2>Ireceipt</h2><em>Limit alert</em><br/><p>Your "+time.toLowerCase()+" expenses have reached "+current+" and exceeded the "+limit+" limit.<br>"+text+"<br> click <a href=\\'/settings.jsp\\'>here</a> to change this alert or add new alerts.<br>To <U>permanetly remove</U> this alert click <a href=\\'/alerts?index="+t+"\\'>here</a> .</p>');\n");
				boo=false;
			}
			}
		}%>


}
</script>
<%}%>
</head>
<body>
        
<div id="wrapper">
    <div id="header">

        <div id="logo">
        </div> 
        <form id="searchform" method="get"  action="/listview.jsp">
<fieldset class="search">
	<input type="text" name="search" class="box" />
	<button class="btn" title="Submit Search">Search</button>
</fieldset>
</form>
        <div id="login">
        <div id="loginwelcome">
		<%
   
    if (user != null) {
%>
<p>Hello, <%= user.getNickname() %>! (You can
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
    } else {
response.sendRedirect("/index.jsp"); }
%></div>
        </div>

            <ul id="navigation">

                <li class="on"><a href="/index.jsp">Home</a></li>
                <li class="current"><a href="/listview.jsp">Receipts</a></li>
                <li class="on"><a href="stats.jsp">Stats</a></li>
                <li class="on"><a href="/newrec.jsp">Add +</a></li>
                 <li class="on"><a href="/settings.jsp">Settings</a></li>
            

            </ul>
    <div id="content">
    <%
	if (request.getParameter("id")==null)
		response.sendRedirect("/index.jsp");
	String id_param=request.getParameter("id");
	long id=Long.parseLong(id_param.substring(id_param.lastIndexOf('(')+1,id_param.lastIndexOf(')')));
    iReceipt rec=(iReceipt)pm.getObjectById(iReceipt.class,id);
	    pm.close();
%>
	
	<div id=caption> <%=rec.getStoreName()%> (<%=rec.getDate_s()%>)</div>
    <div id=caption_small>Total:<%=rec.getTotal()%>, Category:<%=rec.getCategory()%>
    <p></p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
    </div>
    
<table class="big_table" align="center"><tr><td class="top">
<%if ((request.getParameter("edit")!=null)&&(request.getParameter("edit").equals("true"))){%>
	
	<div id="csc-form">
<div class=notes-form>
</div><br>
<div class=notes>Edit receipt:</div>
<form name="personal" action="/UpdateReceiptEntryServlet" method="post" onSubmit="return checkform()" enctype='multipart/form-data'>
Business name:<br>
<input type=text size=20 name=business Value="<%=rec.getStoreName()%>">
<br>Total amount:<br>
<input type=text size=20 name=total Value="<%=rec.getTotal()%>" onBlur="testNumber(this,<%=rec.getTotal()%>)">
<br>Category:<br>
<select name=category>
<%String select="";
for(int i=0;i<Statics.categories.length;i++){
	if (rec.getCategory().equals(Statics.categories[i]))
		select=select+"<option SELECTED>"+Statics.categories[i]+"</option>";
	else
		select=select+"<option>"+Statics.categories[i]+"</option>";
}
out.print(select);
%>
</select><br>
Select date (MM/dd/yyyy):<br>
<INPUT TYPE="text" NAME="date" VALUE="<%=rec.getDate_s()%>" SIZE=25 onClick="cal.select(document.forms['personal'].date,'anchor1','MM/dd/yyyy'); return false;">

<A HREF="#"

   onClick="cal.select(document.forms['personal'].date,'anchor1','MM/dd/yyyy'); return false;"

   NAME="anchor1" ID="anchor1">select</A><br>
   <input type="checkbox" name="imageupdate" value="imageupdate" onClick="enable()"> Update receipt image:<br>
   <input type="file" name="file" disabled>
   <br>
   
   Notes:<br>

                                                                                                                                                                                                                                                                                                                           
      <div><textarea name="notes" rows="3" cols="30" ><%=rec.getNotes()%></textarea></div>
      <input type="checkbox" name="flaged" value="flaged" <%if (rec.isFlaged()) out.print("checked");%>> Flaged<br><br>
      <input type = "hidden" name = "id" value = "<%out.print(id);%>">
      <div><input type="submit" value="Save changes"></div>                                                                                                                                                                                                                                                  
</form>
 <span class="tr-form"> </span><!--top_right-->
 <span class="bl-form"></span><!--bottom_left-->
 <span class="br-form"></span><!--bottom_right-->
</div><!--csc--></td>
   
    

	
<%}else{%>
<div id="csc">
<div class=notes>Notes:</div><br><%=rec.getNotes()%><!--the_box-->

 <span class="tr"> </span><!--top_right-->
 <span class="bl"></span><!--bottom_left-->
 <span class="br"></span><!--bottom_right-->
</div><!--csc--><br>
<table align="center" style="padding-right:12%;"> <tr><td>
<form action="/recview.jsp?id=<%=id_param%>&edit=true" method="post">
      <div class="edit"><input type="submit" value="Edit" /></div>
    </form>
    
    </td>
    <td>
    <form action="/deletereceipt?delete=1&delete0=<%=rec.getUnique()%>" method="post">
      <div class="edit"><input type="submit" value="Delete" /></div>
    </form>
    </td>
    </tr></table>
</td>


      <%}%>
<td class="rec"><img src="<%if (rec.getFilepath()!=null){
	out.write("/getimage?index="+rec.getUnique());}else out.write("/images/no-image.gif");%>" width="400"></td></tr></table>
</div>
    <div id="footer">
        <div id="copyright">&copy; 2010 All Rights Reserved. Designed by the iReceipt team.</a>
    </div>
</div>

</body>
</html>

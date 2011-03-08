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
	
if (document.getElementsByName("sync_back_receipts")[0].checked==true) 

	document.getElementsByName("sync_back_images")[0].disabled=false;
else
	document.getElementsByName("sync_back_images")[0].disabled=true;
} 

window.onload=enable();
</script>




<script src="mootools.js" type="text/javascript"></script>

<link rel="stylesheet" href="sexyalertbox.css" type="text/css" media="all" />

<script src="sexyalertbox.packed.js" type="text/javascript"></script>





<script type="text/javascript">

window.addEvent('domready', function() {

	test();

});

function test() {

enable();

}
</script>







</head>
<BODY >
        
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
        <div id="loginwelcome"><%
    if (user != null) {
%>
<p>Hello, <%= user.getNickname() %>! (You can
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
    } else {
		pm.close();
response.sendRedirect("/index.jsp"); }
%></div>
        </div>

            <ul id="navigation">

                <li class="on"><a href="/index.jsp">Home</a></li>
                <li class="on"><a href="/listview.jsp">Receipts</a></li>
                <li class="on"><a href="stats.jsp">Stats</a></li>
                <li class="on"><a href="/newrec.jsp">Add +</a></li>
                 <li class="current"><a href="/settings.jsp">Settings</a></li>
                
            

            </ul>
    <div id="content" style="text-align:center;">
      <div id=caption>Settings</div>

      <p>&nbsp;</p>
     <div style="text-align:center;">
     <h3>Site options:</h3>
     <form id="settingsform" method="post"  action="/settings">
      <table align="center">
      
      <tr>
        <td>number of entries per list page</td>
        <td>   <input type="text" name="entries_per_page" value="<%=statics.getEntries_per_page()%>" size="15" onBlur="testNumber(this,'0')" /></td></tr>
      <tr><td>clear all receipts from the site's database</td><td align="center"><input type="button" value="Clear all" ONCLICK="window.location.href='/settings'"></td></tr></table>




    <p>&nbsp;</p>
    <h3>Syncronization options:    </h3>
<table align="center">
      <tr><td> <input type="checkbox" name="sync_back_images" <%if (statics.isSync_back_images()) out.write("checked");%> disabled/> </td><td>send to the mobile device images uploaded to the site's platform<br />
      (disabling this option may help to reduce data transfer costs)</td></tr><tr><td><br /></td></tr>
    <tr><td height="25"> <input type="checkbox" name="sync_back_receipts" <%if (statics.isSync_back_receipts()) out.write("checked");%> onClick="enable()"/> </td>
    <td>send to the mobile device receipts uploaded manually to the site's platform</td></tr></table>

<p>&nbsp;</p>
    <input type="submit" value="save settings" />
    </form>
    <br />


<h3>Alerts manager:</h3> 
Alerts can be set here. Click on the alert to remove it
<div>
<div class="tlist letsGiveItAFixedWidthOf500Pixels">
	<ul class="notchedListItems"> 
    <%int num_of_alerts=statics.getNumber_of_alerts();
	for(int i=1;i<=num_of_alerts;i++){
		%>
    <img src="images/sep.jpg" alt="<br>" />
		<li><a href="/alerts?index=<%=i%>"><b><b><%out.write(" Alert when "+statics.getAlertTime(i) +" expenses exceeds "+statics.getAlertlimit(i)+"<br><p>Alert text: "+statics.getAlertText(i)+"</p>");%></b></b></a></li> <%}%>
	</ul> 
  </div>
</td></tr></table>
<br />
<form id="add_alert" method="post"  action="/alerts">
Alert when <select name="time">
<option value="Monthly">Monthly</option>
  <option value="Yearly">Yearly</option>
  <option value="Total">Total</option>
</select>
expenses exceeds 
<input type="text" name="limit" onBlur="testNumber(this,'0')" />
<br />
alert text (can be left blenk): 
<input type="text" name="text"/>   <input type="submit" value="add alert" />
</form>
</div>
    <p>&nbsp;</p>
    <br />
     </div>
    
    

   
    

<td class="rec">
     <%pm.close();%>
</div>
    <div id="footer">
        <div id="copyright">&copy; 2010 All Rights Reserved. Designed by the iReceipt team.</a>
    </div>
</div>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="singer.iReceipt" %>
<%@ page import="singer.PMF" %>
<%@ page import="google.proj.IDate;" %>
<%@ page import="singer.Statics" %>

<html>
<head>
<%PersistenceManager pm = PMF.get().getPersistenceManager();
Statics statics=null;
UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
	if (user!=null){
	
statics=Statics.loadStatics(pm,user.getEmail());}%>
    <link type="text/css" rel="stylesheet" href="/stylesheets/style.css" />
    
    
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
        <div id="updates">
      
        </div>
            <form id="searchform" method="get"  action="/listview.jsp">
<fieldset class="search">
<%if (user!=null){%>
	<input type="text" name="search" class="box" />
	<button class="btn" title="Submit Search">Search</button>
    <%}else{%>
    <br><br>
    <%}%>
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
%>
<p>Hello guest!
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a> to browse your receipts.
</p>
<%
    }
%></div>
        </div>
<ul id="navigation">

                <li class="current"><a href="#">Home</a></li>
                <li class="on"><a href="/listview.jsp">Receipts</a></li>
                <li class="on"><a href="stats.jsp">Stats</a></li>
                <li class="on"><a href="/newrec.jsp">Add +</a></li>
                <li class="on"><a href="/settings.jsp">Settings</a></li>
 </ul>
<div id="content">
<table class="table3"><tr><td>
<table class="table3"><tr><div id="title">Ireceipt</div><div id="small_title">Smart solution for managing your receipts and expenses.</div></tr><tr><td><h2>Save</h2>
     <p>Gain easy access to your receipts from anywhare at anytime.
     </p></td><td><h2>Manage</h2>
      <p class="small_title">Manage and Update your information online with full synchronization with your Android device.</p></td></tr><tr><td>
      <h2>Analize</h2>
      <p class="small_title">Enjoy smart analization of your data, by different filters.</p></td><td> <h2>It's simple!</h2>
      <p class="small_title">Very easy to use interface. No new account is needed. Just use your google account to sign in. </p></td></tr></table>
 </td><td><img src="images/internet-statistics.png"/></td></tr></table>
     
        
       
     
       <h2>It's FREE!</h2>
  <p class="small_title">We offer our service free of charge!</p>
<br>
<a style="text-align:center;" href="http://code.google.com/p/i-receipt/downloads/detail?name=IreceiptGoogleProj.apk"><h2>Get iReceipt for Android right now!</h2></a><br>
</div>
    <div id="footer">
        <div id="copyright">&copy; 2010 All Rights Reserved. Designed by the iReceipt team.</a>
    </div>
</div>
</div>
</div>
</body>
</html>

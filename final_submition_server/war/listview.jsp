<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="javax.jdo.Query"%>
<%@ page import="singer.iReceipt" %>
<%@ page import="singer.PMF" %>
<%@ page import="google.proj.IDate" %>
<%@ page import="java.lang.Integer"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.ArrayList" %>
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
    
	<%long num_of_entries=-1;%>
    <script type="text/javascript">
	function delete_f(){

		document.getElementById('DeleteForm').submit() ;
		}
	</script>
    
 <%int prev_page=0; 
		if(request.getParameter("last_page_to")!=null)
		prev_page=Integer.parseInt(request.getParameter("last_page_to")); %>
        
<div id="wrapper">
    <div id="header">

        <div id="logo">
        </div>
        <div id="updates">
      
        </div>
           <form id="searchform" method="get"  action="/listview.jsp">
<fieldset class="search">
	<input type="text" name="search" class="box" />
	<button class="btn" title="Submit Search" type="submit" >Search</button>
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
            
    <%
	String search = request.getParameter("search");
	long resaults_per_page=statics.getEntries_per_page();
	
	
	
     Query query = pm.newQuery(iReceipt.class);
	 query.setFilter("user_id == id");
	 query.declareParameters("String id");
	 List<iReceipt> receipts=null;
 	 if(num_of_entries==-1){
		    receipts = (List<iReceipt>) query.execute(user.getEmail());
			if (search!=null){
			receipts=new ArrayList(receipts);
			for (Iterator iterator = receipts.iterator(); iterator.hasNext();) {
			iReceipt r = (iReceipt) iterator.next();
			if (!r.containsField(search))
				iterator.remove();
		}
			}
		 if (receipts.isEmpty()) 
     		 num_of_entries=0;
		 else
		     num_of_entries=receipts.size();
	 }
	 if (search==null){
	 query.setRange(prev_page*resaults_per_page,(prev_page+1)* resaults_per_page);
	 receipts = (List<iReceipt>) query.execute(user.getEmail());
	 }
   
	
	
	
	int i=0;
    if (receipts.isEmpty()) {
%> <div id="content">

<p>no receipts</p>
<%
    } else {%>
     <div id="content">
   <div id="itsthetable"><table width="80%" align="center" summary="receits"><caption>
   <%if (search==null) out.write("Receipts List:"); else out.write("Search resaults:");%>
   
    </caption>
    <form name="DeleteForm" id="DeleteForm"  action="/deletereceipt" method="post">
    <input type="hidden" name="delete" value="<%=(resaults_per_page+1)%>">
     
    <thead><tr><th scope="col">Date</th><th scope="col">Store Name</th><th scope="col">Total</th><th scope="col">Category</th><th scope="col"></th></tr></thead><tfoot><tr><th scope="row">Total:</th><td colspan="2" style="text-align:center;"><%=num_of_entries%> receipts</td><td style="width:20px;"><% if (search==null){%><img src="/images/csvreports.gif"> <a href="/CrvexportServlet">download as csv</a><%}%></td>
    <td style="text-align:center;"><a href="javascript:delete_f()">delete</a></td></tr></tfoot>
<tbody>
	<%for (iReceipt r : receipts) {
		if (i%2==0){%>
			 <tr>
           <%}else{%>
           	<tr class="odd">
            <%}%>
             <Th scope="row"><a href="<%="/recview.jsp?id="+pm.getObjectId(r)%>"><%=r.getDate_s()%></a></th><td><%=r.getStoreName()%></td><td><%=r.getTotal()%></td><td><%=r.getCategory()%></td><td style="text-align:center;"><input type="checkbox" id="<%="delete"+i%>" name="<%="delete"+i%>" value="<%=r.getUnique()%>"></td></tr>
             
 <%i++;
        }%>
        </tbody>
        
        </form>
        
    </table></div>
        <div id=tnav>
        <table align="center" class="table2">
        <tr><th scope="col"><%if(prev_page!=0) {%><a href="<%="/listview.jsp?last_page_to="+(prev_page-1)%>">prev</a><%}%></th><th scope="col"> page <%=((prev_page+1)+" of "+(num_of_entries/resaults_per_page+1))%></th><th scope="col"><%if((num_of_entries/resaults_per_page+1)!=(prev_page+1)){%><a href="<%="/listview.jsp?last_page_to="+(prev_page+1)%>">next</a><%}%></th></tr>
        </table>
      </div>
        <%
    }
    pm.close();
%>
    
    
    
    
     </div>
      

    </div>
    <div id="footer">
        <div id="copyright">&copy; 2010 All Rights Reserved. Designed by the iReceipt team.</a>
    </div>
</div>

</body>
</html>

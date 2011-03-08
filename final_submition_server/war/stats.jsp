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
<%@ page import="java.util.Calendar" %>
<%@ page import="singer.store" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%//@ page import="singer.compareDouble" %>
<%@ page import="singer.DateAndCost" %>
<%@ page import="java.util.ArrayList" %>

<%@ page import=" java.util.Collections" %>
<%@ page import=" java.util.Comparator" %>
<%@ page import=" java.util.Iterator" %>
<%@ page import=" java.util.Set" %>
<%@ page import=" java.util.Map.Entry" %>
<%@ page import=" java.util.LinkedHashMap" %>
<%@ page import="singer.Statics" %>
<%@ page import=" java.util.LinkedList" %>

<html>
<head>
<%PersistenceManager pm = PMF.get().getPersistenceManager();
Statics statics=null;
UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
	if (user!=null){
	
statics=Statics.loadStatics(pm,user.getEmail());}%>

    <link type="text/css" rel="stylesheet" href="/stylesheets/secondary_style.css" />
    
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
response.sendRedirect("/index.jsp"); }
%></div>
        </div>

            <ul id="navigation">

                <li class="on"><a href="/index.jsp">Home</a></li>
                <li class="on"><a href="/listview.jsp">Receipts</a></li>
                <li class="current"><a href="stats.jsp">Stats</a></li>
                <li class="on"><a href="/newrec.jsp">Add +</a></li>
                 <li class="on"><a href="/settings.jsp">Settings</a></li>
            

            </ul>
    <div id="content">


<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
    <%
    Query query = pm.newQuery(singer.iReceipt.class);
	query.setFilter("user_id == id");
	query.declareParameters("String id");	
	List<singer.iReceipt> receipts=(List<singer.iReceipt>)query.execute(user.getEmail());

	double sum_shop=0,sum_dining=0,sum_travel=0,sum_car=0;
	double sum_rent=0,sum_groc=0,sum_present=0,sum_enter=0,sum_house=0,sum_other=0;

		for (iReceipt rec:receipts){
			if (rec.getCategory().equals("Shopping")){
				sum_shop+=rec.getTotal();
			}
			if (rec.getCategory().equals("Dining")){
				sum_dining+=rec.getTotal();
			}
			if (rec.getCategory().equals("Travel")){
				sum_travel+=rec.getTotal();
			}
			if (rec.getCategory().equals("Car")){
				sum_car+=rec.getTotal();
			}
			if (rec.getCategory().equals("Rent")){
				sum_rent+=rec.getTotal();
			}
			if (rec.getCategory().equals("Groceries")){
				sum_groc+=rec.getTotal();
			}
			if (rec.getCategory().equals("Presents")){
				sum_present+=rec.getTotal();
			}
			if (rec.getCategory().equals("Entertainment")){
				sum_enter+=rec.getTotal();
			}
			if (rec.getCategory().equals("Household goods")){
				sum_house+=rec.getTotal();
			}
			if (rec.getCategory().equals("Other")){
				sum_other+=rec.getTotal();
			}
		}

    pm.close();
%>
      // Load the Visualization API and the piechart package.
      google.load('visualization', '1', {'packages':['corechart']});
      
      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(drawChart);
      
      // Callback that creates and populates a data table, 
      // instantiates the pie chart, passes in the data and
      // draws it.
      function drawChart() {

      // Create our data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string');
        data.addColumn('number');
        data.addRows([
          ['Shopping',<%=sum_shop%>],
          ['Dining', <%=sum_dining%>],
          ['Travel', <%=sum_travel%>],
		  ['Car',<%=sum_car%>],
          ['Rent',<%=sum_rent%>],
		  ['Groceries',<%=sum_groc%>],
		  ['Presents',<%=sum_present%>],
		  ['Entertainment',<%=sum_enter%>],
		  ['Household goods',<%=sum_house%>],
		  ['Other',<%=sum_other%>],
        ]);

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('chart_cat'));
		<% 	double sum_of_all= sum_shop+ sum_dining+ sum_travel+ sum_car+
								sum_rent+ sum_groc+ sum_present+ sum_enter+ sum_house+ sum_other;
			if (sum_of_all!=0.0) {%>


        chart.draw(data, {width:600, height: 360, is3D: true, title: 'Amount of Money Spent by Category'});
		<%} %>
      }
    </script>
    
 
 
    <script type="text/javascript">
	<%
			
			Double temp_cost;
			String value;
			int k;
			Map<String,Double> stores = new HashMap<String,Double>();
			for (iReceipt rec:receipts){
				if (stores.containsKey(rec.getStoreName())){
					temp_cost=stores.get(rec.getStoreName())+rec.getTotal();
					stores.remove(rec.getStoreName());
					stores.put(rec.getStoreName(),temp_cost);
				}
				else{
					stores.put(rec.getStoreName(),rec.getTotal());
				}
			}
			
		     List list = new ArrayList(stores.entrySet());
		     Collections.sort(list, new Comparator() {
		          public int compare(Object o1, Object o2) {
		               return ((Comparable) ((Map.Entry) (o2)).getValue())
		              .compareTo(((Map.Entry) (o1)).getValue());
		          }
		     });

		    Map result = new LinkedHashMap();
		    for (Iterator it = list.iterator(); it.hasNext();) {
		        Map.Entry entry = (Map.Entry)it.next();
		        result.put(entry.getKey(), entry.getValue());
		    }
		    store[] storesArr= new store[10];
		    int mapsize1=result.size();
		    Set st = result.keySet();
		    int counter=0;
		    for (Object key3 : result.keySet()){
		    	if ((counter==10) || (counter==mapsize1)){
		    		break;
		    	}
		    	else{
		    		storesArr[counter]= new store((String)key3, (Double)result.get(key3));		    		
		    	}
		    	counter++;
		    }
			if (counter<10){
				for (int lastcount=counter;lastcount<10;lastcount++ ){
					storesArr[lastcount]= new store();
				}
			}
			
	%>
	
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Store Name');
        data.addColumn('number', 'Total Expenses in The Store');
        data.addRows(10);
        data.setValue(0, 0, "<%=storesArr[0].getName()%>");
        data.setValue(0, 1, <%=storesArr[0].getCost()%>);
		data.setValue(1, 0, "<%=storesArr[1].getName()%>");
        data.setValue(1, 1, <%=storesArr[1].getCost()%>);
		data.setValue(2, 0, "<%=storesArr[2].getName()%>");
        data.setValue(2, 1, <%=storesArr[2].getCost()%>);
        data.setValue(3, 0, "<%=storesArr[3].getName()%>");
        data.setValue(3, 1, <%=storesArr[3].getCost()%>);
		data.setValue(4, 0, "<%=storesArr[4].getName()%>");
        data.setValue(4, 1, <%=storesArr[4].getCost()%>);
		data.setValue(5, 0, "<%=storesArr[5].getName()%>");
        data.setValue(5, 1, <%=storesArr[5].getCost()%>);
		data.setValue(6, 0, "<%=storesArr[6].getName()%>");
        data.setValue(6, 1, <%=storesArr[6].getCost()%>);
		data.setValue(7, 0, "<%=storesArr[7].getName()%>");
        data.setValue(7, 1, <%=storesArr[7].getCost()%>);
		data.setValue(8, 0, "<%=storesArr[8].getName()%>");
        data.setValue(8, 1, <%=storesArr[8].getCost()%>);
		data.setValue(9, 0, "<%=storesArr[9].getName()%>");
        data.setValue(9, 1, <%=storesArr[9].getCost()%>);

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div3'));
		<% if (counter!=0) { %>
        chart.draw(data, {width: 800, height: 480, title: 'Top 10 Stores by Total Expenses',
                          hAxis: {title: 'Store Name', titleTextStyle: {color: 'red'}}
                         });
		<% }%>
      }
    </script>
    
    <script type='text/javascript'>
	
	<%
		Map<IDate,Double> datesCosts = new HashMap<IDate,Double>();
		Double costs;
		Boolean boo=false;
		Double tempC=0.0;
		IDate id= new IDate();
		for (iReceipt rec:receipts){
			if (datesCosts.size()>0){
				for (IDate k1 : datesCosts.keySet()){
					if (rec.getRdate().equals(k1)){
						boo=true;
						tempC=datesCosts.get(k1);
						id=k1;
					}
				}
			}
			if (boo){
				costs = tempC+rec.getTotal();
				datesCosts.remove(id);
				datesCosts.put(rec.getRdate(),costs);
			}
			else{
				datesCosts.put(rec.getRdate(),rec.getTotal());
			}
			boo=false;
		}
		
		int mapSize= datesCosts.size();
		DateAndCost[] arrayDateandCost = new DateAndCost[mapSize];
		int place=0;
		for (IDate keys : datesCosts.keySet()) {
			arrayDateandCost[place]=new DateAndCost(keys,datesCosts.get(keys));
			place++;
		}
		
	%>
	
      google.load('visualization', '1', {'packages':['annotatedtimeline']});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('date', 'Date');
        data.addColumn('number', 'Expense');
        data.addColumn('string', 'title1');
        data.addColumn('string', 'text1');
		
		<%for(int pl=0;pl<arrayDateandCost.length;pl++){
			 out.print("data.addRows([[new Date("+arrayDateandCost[pl].getDate().getYear()+","+ (arrayDateandCost[pl].getDate().getMonth()-1) +", "+ arrayDateandCost[pl].getDate().getDay()+"),"+arrayDateandCost[pl].getCost()+",undefined, undefined]]);");}%>


        var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div4'));
		<%	if (mapSize!=0) {%>
		chart.draw(data, {displayAnnotations: true});
		<%} %>
      }
    </script>
    
        
    
   
		<%
		float [] thisYear= new float[12];
		float [] LastYear= new float[12];
		for (int q=0;q<12;q++){
			thisYear[q]=(float) 0.0;
			LastYear[q]=(float) 0.0;
		}
		Calendar cal2 = Calendar.getInstance();
		int thisyear = cal2.get(Calendar.YEAR);
		for (iReceipt rec:receipts){
			if (rec.getRdate().getYear()==thisyear){
				thisYear[rec.getRdate().getMonth()-1]+= (float) rec.getTotal();}
			else{
			if (rec.getRdate().getYear()==(thisyear-1)){
				LastYear[rec.getRdate().getMonth()-1]+= (float) rec.getTotal();
				}
			}
		}
		%>
        
	 <script type="text/javascript">
	  google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Month');
        data.addColumn('number', '<%=thisyear%> - This Year');
        data.addColumn('number', '<%=thisyear -1%> - Last Year');
        data.addRows(12);
        data.setValue(0, 0, '1');
        data.setValue(0, 1, <%=thisYear[0]%>);
        data.setValue(0, 2, <%=LastYear[0]%>);
        data.setValue(1, 0, '2');
        data.setValue(1, 1, <%=thisYear[1]%>);
        data.setValue(1, 2, <%=LastYear[1]%>);
        data.setValue(2, 0, '3');
        data.setValue(2, 1, <%=thisYear[2]%>);
        data.setValue(2, 2, <%=LastYear[2]%>);
        data.setValue(3, 0, '4');
        data.setValue(3, 1, <%=thisYear[3]%>);
        data.setValue(3, 2, <%=LastYear[3]%>);
		data.setValue(4, 0, '5');
        data.setValue(4, 1, <%=thisYear[4]%>);
        data.setValue(4, 2, <%=LastYear[4]%>);
		data.setValue(5, 0, '6');
        data.setValue(5, 1, <%=thisYear[5]%>);
        data.setValue(5, 2, <%=LastYear[5]%>);
		data.setValue(6, 0, '7');
        data.setValue(6, 1, <%=thisYear[6]%>);
        data.setValue(6, 2, <%=LastYear[6]%>);
		data.setValue(7, 0, '8');
        data.setValue(7, 1, <%=thisYear[7]%>);
        data.setValue(7, 2, <%=LastYear[7]%>);
		data.setValue(8, 0, '9');
        data.setValue(8, 1, <%=thisYear[8]%>);
        data.setValue(8, 2, <%=LastYear[8]%>);
		data.setValue(9, 0, '10');
        data.setValue(9, 1, <%=thisYear[9]%>);
        data.setValue(9, 2, <%=LastYear[9]%>);
		data.setValue(10, 0, '11');
        data.setValue(10, 1, <%=thisYear[10]%>);
        data.setValue(10, 2, <%=LastYear[10]%>);
		data.setValue(11, 0, '12');
        data.setValue(11, 1, <%=thisYear[11]%>);
        data.setValue(11, 2, <%=LastYear[11]%>);

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div6'));
		<%	float checksum=0;
		for (int run=0; run<12;run++){
			checksum=checksum+thisYear[run]+LastYear[run];
		}
		if (checksum!=0.0) {%>
        chart.draw(data, {width: 800, height: 480, title: 'This Year Expenses in Comparison with Last Year ',
                          hAxis: {title: 'Month', titleTextStyle: {color: 'red'}}
                         });
		<%} %>

      }
    </script>
    
    
     <script type="text/javascript">
		 google.load('visualization', '1', {'packages':['corechart']});
		  
		  // Set a callback to run when the Google Visualization API is loaded.
		  google.setOnLoadCallback(drawChart);
		  
		  // Callback that creates and populates a data table, 
		  // instantiates the pie chart, passes in the data and
		  // draws it.
		  function drawChart() {
	
		  // Create our data table.
			var data = new google.visualization.DataTable();
			data.addColumn('string');
			data.addColumn('number');
			data.addRows([
			  ['January',<%=LastYear[0]%>],
			  ['February',<%=LastYear[1]%>],
			  ['March', <%=LastYear[2]%>],
			  ['April',<%=LastYear[3]%>],
			  ['May',<%=LastYear[4]%>],
			  ['June',<%=LastYear[5]%>],
			  ['July',<%=LastYear[6]%>],
			  ['august',<%=LastYear[7]%>],
			  ['September',<%=LastYear[8]%>],
			  ['October',<%=LastYear[9]%>],
			  ['November',<%=LastYear[10]%>],
			  ['December',<%=LastYear[11]%>],
			]);
			
			// Instantiate and draw our chart, passing in some options.
			var chart = new google.visualization.PieChart(document.getElementById('chart_div7'));
			
			<%	float checksumlast=0;
				for (int run=0; run<12;run++){
					checksumlast=checksumlast+ LastYear[run];
			
				}
				if (checksumlast>0.0) {%>
			chart.draw(data, {width:600, height: 360, is3D: true, title: '<%=thisyear-1%> - Last Year`s Expenses Devided by Month'});
			<%} %>
		  }
		</script>
		
    
    <div id="caption"> Statistics:<p> Expenses This month: <%=statics.getMonthlyxpenses()%> &#8226; This year  <%=statics.getYearlyxpenses()%> &#8226; Total: <%=statics.getTotalexpenses()%> </p></div>
    <div align="center" id="chart_cat"></div>
    
	<div align="center" id="chart_div3"></div>
    <div align="center" id="chart_div5"></div>
    <div align="center" id="chart_div6"></div>
    <div align="center" id="chart_div7"></div>
    <br>
    <div align="center"  id="chart_div4" style='width: 800px; height: 360px;'></div>

    
    </div>
    
    
    <div id="footer">
        <div id="copyright">&copy; 2010 All Rights Reserved. Designed by the iReceipt team.</a>
    </div>
</div>

</body>
</html>

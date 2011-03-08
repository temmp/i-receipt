package singer;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.jdo.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Alerts extends HttpServlet {

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/listview.jsp");
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Statics statics =Statics.loadStatics(pm, user.getEmail());
		String time=req.getParameter("time");
		String limit=req.getParameter("limit");
		String text=req.getParameter("text");
		if (limit==null||limit.equals(""))
			limit="0";
		if (text==null)
			text="";
		statics.addAlert(time, Double.parseDouble(limit), text);
		statics.saveStatics(pm);
		pm.close();

		resp.sendRedirect("/settings.jsp");

		
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/listview.jsp");
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Statics statics =Statics.loadStatics(pm, user.getEmail());
		String index=req.getParameter("index");
		statics.removeAlert(Integer.parseInt(index));
		statics.saveStatics(pm);
		pm.close();

		resp.sendRedirect("/settings.jsp");
	}
	/*
	public static double[] checkAlerts(PersistenceManager pm){
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		Statics statics =Statics.loadStatics(pm, user.getEmail());
		Query query = pm.newQuery(iReceipt.class);
		 query.setFilter("user_id == id");
		 query.declareParameters("String id");
		 List<iReceipt> receipts= (List<iReceipt>) query.execute(user.getEmail());
		 
	}
	*/
}

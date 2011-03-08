package singer;

import java.io.IOException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Settings extends HttpServlet {

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/index.jsp");
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Statics statics =Statics.loadStatics(pm, user.getEmail());
		statics.setEntries_per_page(Math.round(Double.parseDouble((req.getParameter("entries_per_page")))));
		statics.setSync_back_images(req.getParameter("sync_back_images")!=null);
		statics.setSync_back_receipts(req.getParameter("sync_back_receipts")!=null);
		statics.saveStatics(pm);
		pm.close();
		resp.sendRedirect("/settings.jsp");

		
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/index.jsp");
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Statics statics =Statics.loadStatics(pm, user.getEmail());
		Query query =pm.newQuery(singer.iReceipt.class);
		query.setFilter("user_id==id");
		query.declareParameters("String id");
		List<singer.iReceipt> serverlist=(List<singer.iReceipt>)query.execute(user.getEmail());
		List<singer.iReceipt> copy = new ArrayList<singer.iReceipt>();
		for (iReceipt rec : serverlist) {
			copy.add(rec);
		}
		for (iReceipt rec : copy) {
			pm.deletePersistent(rec);
			
		}
		statics.resetExpenses();
		statics.saveStatics(pm);
		pm.close();
		resp.sendRedirect("/listview.jsp");
		}
	
	
}

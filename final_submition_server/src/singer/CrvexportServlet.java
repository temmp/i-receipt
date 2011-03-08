package singer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import singer.iReceipt;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


@SuppressWarnings("serial")
public class CrvexportServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
		   resp.sendRedirect("/index.jsp");
		   return;
		}
		resp.setContentType("Content-type: text/csv");
		resp.setHeader("Content-Disposition", "attachment; filename=data.csv");
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    Query query = pm.newQuery(iReceipt.class);
		query.setFilter("user_id == id");
		query.declareParameters("String id");
		//query.setOrdering("Date_s descending");
  		List<iReceipt> receipts=(List<iReceipt>)query.execute(user.getEmail());
  		
		StringBuffer stbuff= new StringBuffer("Business,Date,Total,Category,Notes\n");
		for (iReceipt rec:receipts){
			stbuff.append(rec.getStoreName().replaceAll(",", " ")+","
					+rec.getDate_s().replaceAll("-", "/")+","+rec.getTotal()+","
					+rec.getCategory()+","+rec.getNotes().replaceAll(","," ")+"\n");
		}
		String st = stbuff.toString();
		byte[] csv = st.getBytes();
		resp.setContentLength(csv.length);
		ServletOutputStream out = resp.getOutputStream();
		out.write(csv);
		out.close();
		pm.close();
	}
}

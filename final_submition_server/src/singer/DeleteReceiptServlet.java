package singer;
import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;



import singer.PMF;
import sun.swing.AccumulativeRunnable;

@SuppressWarnings("serial")
public class DeleteReceiptServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String account=req.getParameter("account");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/listview.jsp");
			return;
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		google.proj.SyncServlet.deleteReceipts(req, pm, user.getEmail(),true);
		resp.sendRedirect("/listview.jsp");
		pm.close();
		}

	}

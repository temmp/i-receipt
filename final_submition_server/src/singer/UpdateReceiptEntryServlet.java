package singer;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UpdateReceiptEntryServlet extends HttpServlet{
/*
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/rec_list.jsp");
			return;
		}

		if (req.getParameter("id")==null){
			resp.sendRedirect("/rec_list.jsp");
			return;
		}

		long id=Long.parseLong(req.getParameter("id"));	        	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			iReceipt rec=pm.getObjectById(iReceipt.class,id);
			if (rec.getUserID().equals(user.getEmail())){
				if (req.getParameter("id")==null){
					resp.sendRedirect("/rec_list.jsp");
					return;
				}
				rec.setStoreName(req.getParameter("business"));
				rec.setTotal(Double.parseDouble(req.getParameter("total")));
				rec.setCategory(req.getParameter("category"));
				rec.setDate_s(req.getParameter("date").replaceAll("/", "-"));
				rec.setNotes(req.getParameter("notes"));
				if((req.getParameter("flaged")!=null)&&(req.getParameter("flaged").equals("flaged")))
					rec.setFlaged(true);
				else
					rec.setFlaged(false);
				rec.setUpdate();
				pm.makePersistent(rec);
				pm.currentTransaction().commit();
			}
		}
		catch (Exception e) {
			pm.currentTransaction().rollback();
			 throw new RuntimeException(e);
		}
		finally{
			pm.close();
		}

		resp.sendRedirect("/recview.jsp?id=("+id+")");
	}
	*/
	
	//to prevent code duplication we use function from recUploadServlet to retreive the
	//data from the fom
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		int id=0;
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/rec_list.jsp");
			return;
		}
   	
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Statics statics = Statics.loadStatics(pm, user.getEmail());
		try {
			iReceipt rec_form=recUploadServlet.getformdata(req);
			if (rec_form.getUnique()==null){
				resp.sendRedirect("/rec_list.jsp");
				pm.close();
				return;
			}     
			id=rec_form.getUnique();
			
			iReceipt rec=pm.getObjectById(iReceipt.class,rec_form.getUnique());
			if (rec.getUserID().equals(user.getEmail())){
				statics.setTotalexpenses(statics.getTotalexpenses()-rec.getTotal());
				statics.setMonthlyxpenses(rec.getRdate().getMonth(),rec.getRdate().getYear(),statics.getMonthlyxpenses()-rec.getTotal());
				statics.setYearlyxpenses(rec.getRdate().getYear(),statics.getYearlyxpenses()-rec.getTotal());
				statics.saveStatics(pm);
				
				pm.currentTransaction().begin();
				rec.setFlaged(rec_form.isFlaged());
				rec.setCategory(rec_form.getCategory());
				rec.setDate_s(rec_form.getDate_s());
				rec.setNotes(rec_form.getNotes());
				rec.setStoreName(rec_form.getStoreName());
				rec.setTotal(rec_form.getTotal());
				if (rec_form.getFilepath()!=null)
					rec.setFilepath(rec_form.getFilepath());
			}
			rec.setUpdate();

			pm.makePersistent(rec);
			pm.currentTransaction().commit();
		
			statics.setTotalexpenses(statics.getTotalexpenses()+rec.getTotal());
			statics.setMonthlyxpenses(rec.getRdate().getMonth(),rec.getRdate().getYear(),rec.getTotal());
			statics.setYearlyxpenses(rec.getRdate().getYear(),rec.getTotal());
			
		
	}
	catch (Exception e) {
		pm.currentTransaction().rollback();
		 throw new RuntimeException(e);
	}
	finally{
		statics.saveStatics(pm);
		pm.close();
	}

	resp.sendRedirect("/recview.jsp?id=("+id+")");
}
	
	
	
	
}






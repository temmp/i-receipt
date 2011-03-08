package google.proj;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;



import singer.PMF;
import singer.Statics;
import sun.swing.AccumulativeRunnable;



@SuppressWarnings("serial")
public class SyncServlet extends HttpServlet {
	
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		if (req.getParameter("size")==null){
			resp.sendError(404);
			return;
		}
		int size=Integer.parseInt(req.getParameter("size"));
		/*if (size==0){
			return;
		}
		 */
		String account=req.getParameter("account");

		List<Integer> nodelret=new ArrayList<Integer>();
		
		  int sizeno=Integer.parseInt(req.getParameter("deletenoret"));
		  
		  
		  for (int i=0; i<sizeno;i++){
		    nodelret.add(Integer.parseInt(req.getParameter("deletenoret"+i)));
		  }
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Statics stats=Statics.loadStatics(pm,account);
		Query query =pm.newQuery(singer.iReceipt.class);
		//query.se
		query.setFilter("user_id==id");
		query.declareParameters("String id");

		@SuppressWarnings("unchecked")
		List<singer.iReceipt> serverlist=(List<singer.iReceipt>)query.execute(account);

		boolean add=false;
		try {
			//deal with receipts deleted only on the phone
			
			for (singer.iReceipt re:serverlist){

				 int index=re.getUnique();
			     if (nodelret.contains(index)){
			      pm.currentTransaction().begin();
			      re.setBack(false);
			      pm.makePersistent(re);
			      pm.currentTransaction().commit();
			      
			     }
			}
			
			
			
			
			for (int i = 0; i < size; i++) {
				add=false;
				Blob blob=null;
				String in=req.getParameter(i+"");
				if(in==null){
					resp.sendError(404);
					return;}
				byte[] decoded = Base64.decode(in.getBytes("ASCII"),Base64.DEFAULT);
				iReceipt rec=new iReceipt();
				rec.fromByteArray(decoded);
				//get image if present
				if (req.getParameter(i+"_image") != null){
					decoded = Base64.decode(req.getParameter(i+"_image").getBytes("ASCII"),Base64.DEFAULT);
					blob = new Blob(decoded);
				}
				//rec is a copy of the new uploaded receipt
				singer.iReceipt rec_s=new singer.iReceipt(rec.getRdate(),rec.getStoreName(),rec.getTotal(), rec.getCategory(),rec.isFlaged(),rec.isProcessed(),rec.getNotes(),blob,account,rec.getUniqueIndex());
				//////////////////////////////
				rec_s.setUpdate(rec.getUpdate());
				rec_s.setSyncdate();

				for (singer.iReceipt tmprr:serverlist){
/*
					 int index=tmprr.getUnique();
				     if (nodelret.contains(index)){
				      pm.currentTransaction().begin();
				      tmprr.setBack(false);
				      pm.makePersistent(tmprr);
				      pm.currentTransaction().commit();
				      
				     }
					
					*/
					if (tmprr.equals(rec_s)){
						if (rec_s.getUpdate().after(tmprr.getUpdate())){
							pm.currentTransaction().begin();
							stats.setTotalexpenses(stats.getTotalexpenses()-tmprr.getTotal());
							stats.setMonthlyxpenses(tmprr.getRdate().getMonth(),tmprr.getRdate().getYear(),stats.getMonthlyxpenses()-tmprr.getTotal());
							stats.setYearlyxpenses(tmprr.getRdate().getYear(),stats.getYearlyxpenses()-tmprr.getTotal());
							stats.saveStatics(pm);
							tmprr.setCategory(rec_s.getCategory());
							tmprr.setDate_s(rec_s.getDate_s());
							tmprr.setFlaged(rec_s.isFlaged());
							tmprr.setNotes(rec_s.getNotes());
							tmprr.setProcessed(rec_s.isProcessed());
							tmprr.setRdate(rec_s.getRdate());
							tmprr.setStoreName(rec_s.getStoreName());
							tmprr.setSyncdate();
							tmprr.setTotal(rec_s.getTotal());
							tmprr.setUpdate(rec_s.getUpdate());
							pm.makePersistent(tmprr);
							pm.currentTransaction().commit();
							stats.setTotalexpenses(stats.getTotalexpenses()+tmprr.getTotal());
							stats.setMonthlyxpenses(tmprr.getRdate().getMonth(),tmprr.getRdate().getYear(),stats.getMonthlyxpenses()+tmprr.getTotal());
							stats.setYearlyxpenses(tmprr.getRdate().getYear(),stats.getYearlyxpenses()+tmprr.getTotal());
							//pm.deletePersistent(tmprr);
							//pm.makePersistent(rec_s);
							add=true;
							break;
						}
						add=true; 
					}
				}
				if (!add){
					pm.makePersistent(rec_s);
					stats.setTotalexpenses(stats.getTotalexpenses()+rec_s.getTotal());
					stats.setMonthlyxpenses(rec_s.getRdate().getMonth(),rec_s.getRdate().getYear(),stats.getMonthlyxpenses()+rec_s.getTotal());
					stats.setYearlyxpenses(rec_s.getRdate().getYear(),stats.getYearlyxpenses()+rec_s.getTotal());
					stats.saveStatics(pm);
					add=false;

				}
				//delete
			//	String search;
		
			}


			Query query2 =pm.newQuery(singer.iReceipt.class);
			//query.se
			query2.setFilter("user_id==id");
			query2.declareParameters("String id");

			@SuppressWarnings("unchecked")
			List<singer.iReceipt> serverlist2=(List<singer.iReceipt>)query2.execute(account);

			int i=0;
			resp.addHeader("X-size",i+"");
			ArrayList<String> BinaryRecSendList=new ArrayList<String>();
			for (singer.iReceipt tmprr:serverlist2){

				if (tmprr.getBack()&& tmprr.getUpdate()!=null&& tmprr.getUpdate().after(tmprr.getSyncDate())&&(tmprr.getUnique()>0||(stats.isSync_back_receipts()&&tmprr.getUnique()<0))){
					iReceipt r=new iReceipt(tmprr.getRdate(), tmprr.getStoreName(),tmprr.getTotal(),tmprr.getCategory(),tmprr.isFlaged(),tmprr.isProcessed(),
							tmprr.getNotes(),"",tmprr.getUserID(),tmprr.getUnique());
					if (tmprr.getFilepath()!=null&&stats.isSync_back_images())
						r.setFilepath("filepath");
					r.setUpdate(tmprr.getUpdate());
					r.setSync(tmprr.getSyncDate());
					tmprr.setSyncdate();
					pm.currentTransaction().begin();
					pm.makePersistent(tmprr);
					pm.currentTransaction().commit();
					byte[] by=r.toByteArray();
					byte[]encoded=Base64.encode(by, Base64.DEFAULT);
					String str=new String(encoded,"ASCII");
					//resp.addHeader("X-"+i+"",str);
					BinaryRecSendList.add(str);
					i++;

				}
			}
			
			resp.setHeader("X-size",i+"");
			if (i!=0){
				byte[] listForSend;
				ByteArrayOutputStream fos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(BinaryRecSendList);
				oos.close();
				listForSend=fos.toByteArray();
				resp.setContentLength(listForSend.length);
				resp.setContentType("Content-Type");
				ServletOutputStream stream = resp.getOutputStream();
				stream.write(listForSend);
				stream.flush();
						
				
			}
			
			//send receipts for deletetion
			
			int count=0;
			for (String id:stats.getDeleteReceiptsList()){
				resp.setHeader("delete"+count,id);
				count++;
				
			}
			resp.setHeader("delete",count+"");
			stats.clearDeleteReceiptsList();
			stats.saveStatics(pm);
			
			

			deleteReceipts(req, pm, account,false);

		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			//pm.currentTransaction().rollback();
			throw new RuntimeException(e);
		}
		finally{
			pm.close();
		}

	}

	public static void deleteReceipts(HttpServletRequest req,PersistenceManager pm,String userAccount,boolean deleteOnPhone){
		if (req.getParameter("delete")!=null){
			Statics stats = Statics.loadStatics(pm,userAccount);
			int delete_size = Integer.parseInt(req.getParameter("delete"));
			Query query3 =pm.newQuery(singer.iReceipt.class);
			//query.se
			String quary_str="";
			for (int j = 0; j < delete_size; j++) {
				if (j==0)
					quary_str+="&&(";
				else
					quary_str+="||";
				quary_str+="receiptuniqueindex=="+req.getParameter("delete"+j);	
				if (j==delete_size-1)
					quary_str+=")";
			}
			query3.setFilter("user_id==id"+quary_str);
			query3.declareParameters("String id");

			@SuppressWarnings("unchecked")
			List<singer.iReceipt> serverlist3=(List<singer.iReceipt>)query3.execute(userAccount);
			for (singer.iReceipt rec : serverlist3) {
				if (deleteOnPhone)
					stats.addToDeleteReceiptsList(rec.getUnique()+"");
				stats.setTotalexpenses(stats.getTotalexpenses()-rec.getTotal());
				stats.setMonthlyxpenses(rec.getRdate().getMonth(),rec.getRdate().getYear(),stats.getMonthlyxpenses()-rec.getTotal());
				stats.setYearlyxpenses(rec.getRdate().getYear(),stats.getYearlyxpenses()-rec.getTotal());
				pm.deletePersistent(rec);

			}
			stats.saveStatics(pm);
			

		}
	}


}

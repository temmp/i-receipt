package singer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@PersistenceCapable
public class Statics  implements java.io.Serializable {

	public static String[] categories = { "Dining", "Car", "Travel",
			"Shopping", "Rent", "Groceries", "Presents", "Entertainment",
			"Household goods", "Other" };
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String user;

	@Persistent
	private int UniqueId;

	@Persistent
	private List<String> deleteReceiptsList;

	@Persistent
	private List<String> time;
	@Persistent
	private List<Double> limit;
	@Persistent
	private List<String> text;
	@Persistent
	private long entries_per_page;
	@Persistent
	private boolean sync_back_images;
	@Persistent
	private boolean sync_back_receipts;
	@Persistent
	private double Totalexpenses;
	@Persistent
	private double Monthlyxpenses;
	@Persistent
	private double Yearlyxpenses;
	@Persistent
	private int month;
	@Persistent
	private int year;
	
	
	
	@Persistent
	private int number_of_alerts;

	private Statics(String user) {
		this.setUser(user);
		this.setUniqueId(-1);
		this.deleteReceiptsList = new ArrayList<String>();
		this.entries_per_page = 10;
		this.sync_back_images = true;
		this.sync_back_receipts = true;
		this.time = new ArrayList<String>();
		this.limit = new ArrayList<Double>();
		this.text = new ArrayList<String>();
		this.number_of_alerts=0;
		Date date=new Date();
		this.month=date.getMonth()+1;
		this.year=date.getYear()+1900;
		this.Totalexpenses=0;
		this.Monthlyxpenses=0;
		this.Yearlyxpenses=0;
		

	}

	public static Statics loadStatics(PersistenceManager pm, String user1) {
		Query query = pm.newQuery(singer.Statics.class);
		query.setFilter("user==user1");
		query.declareParameters("String user1");
		List<singer.Statics> statics_list = (List<singer.Statics>) query
				.execute(user1);
		if (statics_list == null){
			Statics statics = new Statics(user1);
			pm.makePersistent(statics);
			return statics;
		}
		if (statics_list.isEmpty()){
			Statics statics = new Statics(user1);
			pm.makePersistent(statics);
			return statics;
		}
		Statics statics=statics_list.get(0);
		Date date=new Date();
		if (date.getMonth()+1!=statics.month){
			statics.month=date.getMonth()+1;
			statics.Monthlyxpenses=0;
		}
		if (date.getYear()+1900!=statics.year){
			statics.year=date.getYear()+1900;
			statics.Yearlyxpenses=0;
		}
		statics.saveStatics(pm);
		
		return statics;
	}

	public void saveStatics(PersistenceManager pm) {
		pm.currentTransaction().begin();
		pm.makePersistent(this);
		pm.currentTransaction().commit();

	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void clearDeleteReceiptsList() {
		this.deleteReceiptsList.clear();
	}

	public void addToDeleteReceiptsList(String id) {
		this.deleteReceiptsList.add(id);
	}

	public List<String> getDeleteReceiptsList() {
		return this.deleteReceiptsList;
	}

	public void setUniqueId(int uniqueId) {
		UniqueId = uniqueId;
	}

	public int getUniqueId() {
		return UniqueId;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setEntries_per_page(long l) {
		this.entries_per_page = l;
	}

	public long getEntries_per_page() {
		return entries_per_page;
	}

	public void setSync_back_images(boolean sync_back_images) {
		this.sync_back_images = sync_back_images;
	}

	public boolean isSync_back_images() {
		return sync_back_images;
	}

	public void setSync_back_receipts(boolean sync_back_receipts) {
		this.sync_back_receipts = sync_back_receipts;
	}

	public boolean isSync_back_receipts() {
		return sync_back_receipts;
	}
	
	public void resetExpenses(){
		this.Yearlyxpenses=0;
		this.Monthlyxpenses=0;
		this.Totalexpenses=0;
	}

	public void addAlert(String time, double limit, String text) {
		if (limit == 0)
			limit++;
		this.limit.add(limit);
		this.text.add(text);
		this.time.add(time);
		this.number_of_alerts++;
	}

	public void removeAlert(int index) {
		this.limit.remove(index-1);
		this.text.remove(index-1);
		this.time.remove(index-1);
		this.number_of_alerts--;
	}

	public String getAlertTime(int index) {
		return this.time.get(index-1);
	}

	public String getAlertText(int index) {
		return this.text.get(index-1);
	}

	public Double getAlertlimit(int index) {
		return this.limit.get(index-1);
	}

	public void setNumber_of_alerts(int number_of_alerts) {
		this.number_of_alerts = number_of_alerts;
	}

	public int getNumber_of_alerts() {
		return number_of_alerts;
	}
	/*
	public checkAlerts(){
	*/

	public void setMonthlyxpenses(int month,int year,double monthlyxpenses) {
		if (year!=this.year)
			return;
		if (month!=this.month)
			return;
		Monthlyxpenses = monthlyxpenses;
		
	}

	public double getYearlyxpenses() {
		return Yearlyxpenses;
	}

	public void setYearlyxpenses(int year,double yearlyxpenses ) {
		if (year!=this.year)
			return;
		Yearlyxpenses = yearlyxpenses;
	}

	public double getMonthlyxpenses() {
		return Monthlyxpenses;
	}

	public void setTotalexpenses(double totalexpenses) {
		Totalexpenses = totalexpenses;
	}

	public double getTotalexpenses() {
		return Totalexpenses;
	}


}

package google.proj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class iReceipt implements Receipt, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IDate Rdate;
	private String StoreName;
	private double Total;
	private String Category;
	private boolean Flaged;
	private boolean processed;
	private String Notes;
	private String filepath;
	private final String receitname;

	public iReceipt(IDate rdate, String storeName, double total,
			String category, boolean flaged, boolean processed, String notes,
			String filepath, String rname) {
		Rdate = rdate;
		StoreName = storeName;
		Total = total;
		Category = category;
		Flaged = flaged;
		this.processed = processed;
		Notes = notes;
		this.filepath = filepath;
		receitname = rname;
	}

	public iReceipt(IDate rdate, String storeName, double total,
			String category, boolean flaged, boolean processed, String notes,
			String filepath) {
		Rdate = rdate;
		StoreName = storeName;
		Total = total;
		Category = category;
		Flaged = flaged;
		this.processed = processed;
		Notes = notes;
		this.filepath = filepath;
		receitname = null;
	}

	public iReceipt(String rname) {
		Rdate = null;
		StoreName = null;
		Total = -1;
		Category = null;
		Flaged = false;
		this.processed = false;
		Notes = null;
		this.filepath = null;
		receitname = rname;
	}

	public iReceipt() {
		Rdate = null;
		StoreName = null;
		Total = -1;
		Category = null;
		Flaged = false;
		this.processed = false;
		Notes = null;
		this.filepath = null;
		receitname = null;
	}

	/*
	 * public iReceipt(){ Rdate = null; StoreName = null; Total = -1; Category =
	 * null; Flaged = false; this.processed = false; Notes = null; this.filepath
	 * = null;
	 * 
	 * }
	 */
	public void setRdate(IDate rdate) {
		Rdate = rdate;
	}

	public IDate getRdate() {
		return Rdate;
	}

	public String getStoreName() {
		return StoreName;
	}

	public void setStoreName(String storeName) {
		StoreName = storeName;
	}

	public void setTotal(double total) {
		Total = total;
	}

	public double getTotal() {
		return Total;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

	public void setFlaged(boolean flaged) {
		Flaged = flaged;
	}

	public boolean isFlaged() {
		return Flaged;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public boolean isProcessed() {
		return processed;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFilepath() {
		return filepath;
	}

	public String toString() {
		return String
				.format("%s\t%-6.6s\t%-8.8s", Rdate, this.Total, StoreName);
		// sprintf(out,"%s.%s.%s\t10.10%s\t%s",Rdate.getDay(),Rdate.getMonth(),Rdate.getYear(),StoreName,this.Total);
		// return
		// (Rdate.getDay()+"."+Rdate.getMonth()+"."+Rdate.getYear()+"\t"+StoreName+"\t"+this.Total);
	}

	public String getplaceofsave() {
		return receitname;
	}
	
	public byte[] toByteArray() throws IOException{
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ObjectOutputStream oos = new ObjectOutputStream(baos);
    	oos.writeObject(this);
    	return baos.toByteArray();
	}
	
	public void fromByteArray(byte[] in) throws StreamCorruptedException, IOException, ClassNotFoundException{
		ByteArrayInputStream bais = new ByteArrayInputStream(in);
		ObjectInputStream ois = new ObjectInputStream(bais);
		iReceipt myObject = (iReceipt) ois.readObject();
		this.Category=myObject.getCategory();
		this.filepath=myObject.getFilepath();
		this.Flaged=myObject.isFlaged();
		this.Notes=myObject.getNotes();
		this.processed=myObject.isProcessed();
		this.Rdate=myObject.getRdate();
		this.StoreName=myObject.getStoreName();
		this.Total=myObject.getTotal();
		
	}
}
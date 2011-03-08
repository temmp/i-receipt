package singer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DelList implements Serializable {
	 List<String> deleteReceiptsList;
	 int count;
	
	public DelList() {
		deleteReceiptsList=new ArrayList<String>();
		count=0;
	}
	
	public DelList( List<String> deleteReceiptsList) {
		this.deleteReceiptsList=deleteReceiptsList;
		count=0;
	}

}

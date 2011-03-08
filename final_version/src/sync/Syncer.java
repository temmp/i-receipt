package sync;

import google.proj.compute_receipt;
import google.proj.iReceipt;
import google.proj.idan;
import google.proj.rec_view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import misc.Misc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;

public class Syncer {
	static String serverAdress="http://receipt1234.appspot.com/";
	boolean alreadysync = false;
	private Date lastsync;
	//List<iReceipt> update_rec_list;
	private String account = null;
	private List<String> delete_rec_list;
	private List<Integer> deletefromphonenoreturn;
	Thread syncthread;
	static Context context;
	Activity activity;


	@SuppressWarnings("unchecked")
	public Syncer(String account,Activity act) {
		//update_rec_list = new ArrayList<iReceipt>();

		delete_rec_list = new ArrayList<String>();
		deletefromphonenoreturn=new ArrayList<Integer>();
		lastsync = new Date(100, 5, 5);
		this.setAccount(account);
		syncthread=new Thread(new SyncThread(),"syncThread");
		this.syncthread.start();
		boolean boo=isAliveSyncThread();
		this.activity=act;
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					act.openFileInput("delete_rec_list.tmp"));

			delete_rec_list = (List<String>) inputStream
			.readObject();
			inputStream.close();
		} catch (IOException ex) {
			delete_rec_list = new ArrayList<String>();
		} catch (ClassNotFoundException e) {
			delete_rec_list = new ArrayList<String>();
		}

		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					act.openFileInput("deletefromphonenoreturn.tmp"));
			deletefromphonenoreturn = (List<Integer>) inputStream
			.readObject();
			inputStream.close();


		} catch (IOException ex) {
			deletefromphonenoreturn=new ArrayList<Integer>();
		} catch (ClassNotFoundException e) {
			deletefromphonenoreturn=new ArrayList<Integer>();
		}

	}

	public void syncNow(Context context){
		this.context=context;
		(new Progress()).execute();

	}


	class Progress extends AsyncTask<Void, Void, Void> {

		protected ProgressDialog progressDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(Syncer.this.context,
					"Ireceipt", "Syncing...", true, true);
			progressDialog.setOnCancelListener(new OnCancelListener() {




				public void onCancel(DialogInterface dialog) {
					//Syncer.this.syncthread.resume();
				}
			});
		}

		protected Void doInBackground(Void... params) {
			//Syncer.this.syncthread.suspend();
			sendSync_h();
			Misc.saveList(activity);
			//Syncer.this.syncthread.resume();
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(null);
			progressDialog.dismiss();


		}

	}



	public void sendSync1() {
		//sendSync(this.update_rec_list);
	}

	public void sendSync_h() {
		ArrayList list = new ArrayList<iReceipt>();
		for (Iterator iterator = idan.rec_arr.iterator(); iterator.hasNext();) {
			iReceipt rec = (iReceipt) iterator.next();
			if (rec.getRdate()==null){
				iterator.remove();
				continue;
			}
			if (rec.getSyncdate()!=null &&rec.getUpdate()!=null&&rec.getSyncdate().before(rec.getUpdate())){
				list.add(rec);
				rec.setSync();
			}

		}



		sendSync(list);
		//sendSync();
	}

	private void sendSync(List<iReceipt> list) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(serverAdress+ "sync");

		// Add your data
		if (list == null)
			return;
		// if (list.isEmpty()&&this.delete_rec_list.isEmpty())
		// return;
		try {
			int i = 0;
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = 3;
			// send the size of the list
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs
			.add(new BasicNameValuePair("size", list.size() + ""));
			for (iReceipt ireceipt : list) {
				// add the receipt
				byte[] by = ireceipt.toByteArray();
				byte[] encoded = Base64.encode(by, Base64.DEFAULT);
				String str = new String(encoded, "ASCII");
				nameValuePairs.add(new BasicNameValuePair(i + "", str));
				// add receipt image
				if (ireceipt.getFilepath() != null
						&& !ireceipt.getFilepath().equals("")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					Bitmap bam = BitmapFactory.decodeFile(
							ireceipt.getFilepath(), bitmapOptions);
					if (bam!=null){
						bam.compress(Bitmap.CompressFormat.JPEG, 70, baos);
						byte[] b = baos.toByteArray();
						encoded = Base64.encode(b, Base64.DEFAULT);
						str = new String(encoded, "ASCII");
						nameValuePairs
						.add(new BasicNameValuePair(i + "_image", str));}
					baos.close();
				}
				i++;
			}
			if (!this.delete_rec_list.isEmpty()) {
				nameValuePairs.add(new BasicNameValuePair("delete",
						this.delete_rec_list.size() + ""));
				for (int j = 0; j < this.delete_rec_list.size(); j++) {
					nameValuePairs.add(new BasicNameValuePair("delete" + j,
							this.delete_rec_list.get(j)));
				}
			}

			//nameValuePairs.add(new BasicNameValuePair("deletenoret", 0+""));
			nameValuePairs.add(new BasicNameValuePair("deletenoret",
					this.deletefromphonenoreturn.size()+""));
			if (!this.deletefromphonenoreturn.isEmpty()){


				for (int j=0; j< this.deletefromphonenoreturn.size(); j++){
					nameValuePairs.add(new BasicNameValuePair("deletenoret"+j,
							this.deletefromphonenoreturn.get(j)+""));
				}
			}


			nameValuePairs.add(new BasicNameValuePair("account", String
					.valueOf(account)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			// /////////////////////////////////////////////////////////////
			int size = 0;
			if (response.getHeaders("X-size") != null
					&& response.getHeaders("X-size").length != 0) {
				size = Integer.parseInt(response.getHeaders("X-size")[0]
				                                                      .getValue());
			}
			int delete;
			if (response.getHeaders("delete").length == 0
					|| response.getHeaders("delete")[0].getValue() == null)
				delete = 0;
			else
				delete = Integer.parseInt(response.getHeaders("delete")[0]
				                                                        .getValue());
			int delete_counter = 0;
			boolean newrec=false;

			/////////////////
			ArrayList<String> ReceivedRecList = null;
			if (size>0){


				HttpEntity r_entity = response.getEntity();
				if( r_entity != null ) {
					byte[] byteEncodedList=new byte[(int) r_entity.getContentLength()];
					if(r_entity.isStreaming()) {
						DataInputStream is = new DataInputStream(r_entity.getContent());
						is.readFully(byteEncodedList);


						ByteArrayInputStream fos = new ByteArrayInputStream(byteEncodedList);
						ObjectInputStream oos = new ObjectInputStream(fos);
						ReceivedRecList=(ArrayList<String>)oos.readObject();
						oos.close();
					}

				}
				if (ReceivedRecList==null)
					size=0;
			}





			for (int j = 0; j < Math.max(size, delete); j++) {

				newrec=false;
				String in;
				if (ReceivedRecList==null||ReceivedRecList.size()<=j)
					//if (response.getHeaders("X-"+j + "").length == 0)
						in = null;
					else
						in = ReceivedRecList.get(j);

				iReceipt rec = null;
				if (in != null) {
					byte[] decoded = Base64.decode(in.getBytes("ASCII"),
							Base64.DEFAULT);
					rec = new iReceipt();
					rec.fromByteArray(decoded);
				}
				String delete_in;
				if (response.getHeaders("delete" + delete_counter).length == 0)
					delete_in = null;
				else
					delete_in = response.getHeaders("delete" + delete_counter)[0]
					                                                           .getValue();
				iReceipt shoudDelete = null;
				for (iReceipt rr : idan.rec_arr) {
					if (rec != null) {
						if (rr.getUniqueIndex() == rec.getUniqueIndex()) { // same
							// receipt\
							newrec=true;
							if (rec.getUpdate().after(rr.getUpdate())) {
								rr.setCategory(rec.getCategory());
								rr.setFlaged(rec.isFlaged());
								rr.setNotes(rec.getNotes());
								rr.setProcessed(true);
								rr.setRdate(rec.getRdate());
								rr.setStoreName(rec.getStoreName());
								rr.setTotal(rec.getTotal());
								rr.setUpdate(rec.getUpdate());
								rr.setSync(rec.getSyncdate());
								////////////////////////

								if (rec.getFilepath()!=null && !rec.getFilepath().equals("")){

									String path = test(activity,rr.getUniqueIndex(),account);
									rr.setFilepath(path);
									///////////////////////
								}
								//else rr.setFilepath("");



							}
						}

					}
					if (delete_in != null) {
						if ((rr.getUniqueIndex() + "").equals(delete_in)) {
							shoudDelete = rr;

						}
					}
				}

				if (shoudDelete != null) {
					newrec=true;
					idan.rec_arr.remove(shoudDelete);
					delete_counter++;
					shoudDelete = null;
				}

				if (!newrec&&rec!=null){ //new receipt from web
					iReceipt r = new iReceipt(rec.getUniqueIndex());
					r.setCategory(rec.getCategory());
					r.setFlaged(rec.isFlaged());
					r.setNotes(rec.getNotes());
					r.setProcessed(true);
					r.setRdate(rec.getRdate());
					r.setStoreName(rec.getStoreName());
					r.setUpdate(rec.getUpdate());
					r.setSync();
					r.setTotal(rec.getTotal());
					if (rec.getFilepath()!=null &&!rec.getFilepath().equals("")){
						String path = test(activity,r.getUniqueIndex(),account);
						r.setFilepath(path);
					}
					else r.setFilepath("");
					idan.rec_arr.add(r);

				}
			}
			// //////////////////////////////////
			// delete receipts

			lastsync = new Date();
			this.clearDelete_rec_list(activity);
			//this.clearUpdateList();
			this.clereDelete_no_ret(activity);
			alreadysync = true;

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean needtoSync() {
		Date d1 = new Date();
		long l1 = d1.getTime();
		long l2 = lastsync.getTime();
		if ((l1 - l2) >= 86400000) {
			lastsync = new Date();
			return true;
		} else
			return false;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccount() {
		return account;
	}

	public Date getLastsync() {
		return lastsync;
	}

	public Activity getActivity(){
		return this.activity;
	}

	public void setLastsync(Date lastsync) {
		this.lastsync = lastsync;
	}

	public void setDelete_rec_list(List<String> delete_rec_list,Activity act) {
		this.delete_rec_list = delete_rec_list;

		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					act.openFileOutput("delete_rec_list.tmp", Context.MODE_PRIVATE));

			outputStream.writeObject(this.delete_rec_list);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}

	}

	public List<String> getDelete_rec_list() {
		return delete_rec_list;
	}
	public void clereDelete_no_ret(Activity act){
		this.deletefromphonenoreturn.clear();
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					act.openFileOutput("deletefromphonenoreturn.tmp", Context.MODE_PRIVATE));

			outputStream.writeObject(this.deletefromphonenoreturn);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	public void clearDelete_rec_list(Activity act) {
		this.delete_rec_list.clear();
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					act.openFileOutput("delete_rec_list.tmp", Context.MODE_PRIVATE));

			outputStream.writeObject(this.delete_rec_list);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	public void addToDeleteList(String id,Activity act) {
		delete_rec_list.add(id);
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					act.openFileOutput("delete_rec_list.tmp", Context.MODE_PRIVATE));

			outputStream.writeObject(this.delete_rec_list);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}

	public void addToDeleteno_ret(int id,Activity act) {
		deletefromphonenoreturn.add(id);
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					act.openFileOutput("deletefromphonenoreturn.tmp", Context.MODE_PRIVATE));

			outputStream.writeObject(this.deletefromphonenoreturn);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
	}





	public boolean isAliveSyncThread(){
		return this.syncthread.isAlive();
	}


	///////////////////////////////////////////////////
	public static String test(Activity c,int unique,String account) throws IOException {
		URL url;

		url = new URL(serverAdress+ "getimage?index="+unique+"&id="+account);


		InputStream input = null;
		FileOutputStream output = null;
		File photo=null;
		try {
			
			if (idan.settings.getSaveLocation())
				photo = new File(context.getExternalFilesDir(null),
						unique + "ireceipt.jpg");
			else
				photo = new File(context.getFilesDir(),
						unique + "ireceipt.jpg");

			input = url.openConnection().getInputStream();
			output = new FileOutputStream(photo);

			int read;
			byte[] data = new byte[1024];
			while ((read = input.read(data)) != -1)
				output.write(data, 0, read);


		} finally {
			if (output != null)
				output.close();
			if (input != null)
				input.close();
		}
		if (photo==null)
			return "";
		return photo.getAbsolutePath();

	}
	///////////////////////////////////////////////







	public class SyncThread implements Runnable{


		public void run() {
			System.out.println("/n/n/n/n/n/n");
			for(;;){
				sendSync_h();
				Misc.saveList(activity);
				try {
					if (idan.settings.getSyncFrequency()==-1)
						return;
					synchronized(this){
						this.wait(idan.settings.getSyncFrequency());}	
				} catch (InterruptedException e) {
					return;
				}
			}


		}
	}
}
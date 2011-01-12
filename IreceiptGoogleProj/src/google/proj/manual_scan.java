package google.proj;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import google.proj.compute_receipt.MyOnItemSelectedListenerSpinner01;
import google.proj.compute_receipt.MyOnItemSelectedListenerSpinner04;
import google.proj.compute_receipt.save;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class manual_scan extends Activity {

	EditText business, price, notes;
	TextView date;
	private Spinner spinner_cat;
	private ArrayAdapter<String> adapter_cat;
	private iReceipt rec;
	CheckBox myCheckBox;
	private int Year, Month, Day;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manualscan);
		idan.receiptUniqueIndex++;
		rec = new iReceipt(idan.receiptUniqueIndex);
		// rec = new iReceipt();
		rec.setCategory("Dining");
		business = (EditText) findViewById(R.id.EditText01);
		price = (EditText) findViewById(R.id.EditText02);
		notes = (EditText) findViewById(R.id.EditNotesManual);
		date = (TextView) findViewById(R.id.dateView);
		myCheckBox = (CheckBox) findViewById(R.id.CheckBox1);
		spinner_cat = (Spinner) findViewById(R.id.Spinner04);
		adapter_cat = new ArrayAdapter<String>(manual_scan.this,
				android.R.layout.simple_spinner_item, MainActivity.cat);
		adapter_cat
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_cat.setAdapter(adapter_cat);
		spinner_cat
				.setOnItemSelectedListener(new MyOnItemSelectedListenerSpinner04());
		final Calendar c = Calendar.getInstance();
		Year = c.get(Calendar.YEAR);
		Month = c.get(Calendar.MONTH);
		Day = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay(R.id.dateView);
		date.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(R.id.dateView);
			}
		});
	}

	public class MyOnItemSelectedListenerSpinner04 implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Object item = parent.getItemAtPosition(pos);
			String str = (String) item;
			rec.setCategory(str);
		}

		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Year = year;
			Month = monthOfYear;
			Day = dayOfMonth;
			updateDisplay(R.id.dateView);
		}
	};

	private void updateDisplay(int id) { // have only one option - date.
		if ((Month > 8) && (Day > 9))
			date.setText(new StringBuilder().append(Month + 1).append("-")
					.append(Day).append("-").append(Year).append(" "));
		if ((Month > 8) && (Day < 10))
			date.setText(new StringBuilder().append(Month + 1).append("-")
					.append("0" + Day).append("-").append(Year).append(" "));
		if ((Month < 9) && (Day > 9))
			date.setText(new StringBuilder().append("0" + (Month + 1))
					.append("-").append(Day).append("-").append(Year)
					.append(" "));
		if ((Month < 9) && (Day < 10))
			date.setText(new StringBuilder().append("0" + (Month + 1))
					.append("-").append("0" + Day).append("-").append(Year)
					.append(" "));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dateView:
			DatePickerDialog Date1 = new DatePickerDialog(this,
					DateSetListener, Year, Month, Day);
			return Date1;
		}
		return null;
	}

	public void onClick3(View view) {
		String str1;
		int month, day, year;
		// setResult(1);
		rec.setProcessed(true);
		rec.setFlaged(myCheckBox.isChecked());
		rec.setNotes(notes.getText().toString());
		str1 = date.getText().toString();
		month = Integer.parseInt(str1.substring(0, 2));
		day = Integer.parseInt(str1.substring(3, 5));
		year = Integer.parseInt(str1.substring(6, 10));
		rec.setRdate(new IDate(year, month, day));
		rec.setStoreName(business.getText().toString());
		rec.setTotal(Double.parseDouble(price.getText().toString()));
		idan.rec_arr.add(rec);
		(new save()).execute(null);
		//setResult(100);
		// saveList2();
		finish();
	}

	public void onClick4(View view) {
		//setResult(100);
		idan.receiptUniqueIndex--;
		finish();
	}

	/*
	 * public void saveList2() { try { ObjectOutputStream outputStream = new
	 * ObjectOutputStream( openFileOutput("RecListsave.tmp",
	 * Context.MODE_PRIVATE)); outputStream.writeObject(idan.rec_arr);
	 * outputStream.close(); } catch (IOException ex) { ex.printStackTrace(); }
	 * }
	 */

	public void saveList() {

		// ConnectivityManager conMgr =
		// (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		rec.setUpdate();

		/*
		 * boolean connected = ( conMgr.getActiveNetworkInfo() != null &&
		 * conMgr.getActiveNetworkInfo().isAvailable() &&
		 * conMgr.getActiveNetworkInfo().isConnected() );
		 */
		// add rec to update rec list

		// check if the device is connected

		boolean connected = false;
		ConnectivityManager mConnectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !mConnectivity.getBackgroundDataSetting())
			connected = false;
		else {
			int netType = info.getType();
			int netSubtype = info.getSubtype();
			if (netType == ConnectivityManager.TYPE_WIFI) {
				connected = info.isConnected();
			} else if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
					&& !mTelephony.isNetworkRoaming()) {
				connected = info.isConnected();
			} else {
				connected = false;
			}
		}

		if (connected) {
			for (iReceipt tmprr : idan.rec_arr) {

				if (rec_view.notSync(tmprr)) {
					tmprr.setSync();
					idan.sync.addtoUpdateList(tmprr);
				}
			}

			idan.sync.sendSync(loginpage.accountname);
			// need to check if the sync run ok
			// for (iReceipt tmprr: idan.sync.getUpdateList()){
			// tmprr.setSync();
			// }
			idan.sync.deleteupdatelist();
		}

		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					openFileOutput("RecListsave.tmp", Context.MODE_PRIVATE));
			outputStream.writeObject(idan.rec_arr);
			outputStream.close();
			outputStream = new ObjectOutputStream(openFileOutput(
					"recIndexsave.tmp", Context.MODE_PRIVATE));
			outputStream.writeObject((Integer) idan.receiptUniqueIndex);
			outputStream.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}

	class save extends AsyncTask<Void, Void, Void> {

		protected ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(manual_scan.this,
					"Ireceipt", "Saving...", true, false);
		}

		protected Void doInBackground(Void... params) {
			saveList();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(null);
			progressDialog.dismiss();
			finish();

		}

	}
}
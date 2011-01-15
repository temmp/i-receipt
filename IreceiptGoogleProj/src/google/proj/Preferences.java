package google.proj;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getPreferenceManager().setSharedPreferencesName(Preference.this);
		addPreferencesFromResource(R.xml.preferences);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		// getPrefs(); *************************************************

		// Get the custom preference
		/*
		 * Preference customPref = (Preference) findPreference("customPref");
		 * customPref .setOnPreferenceClickListener(new
		 * OnPreferenceClickListener() {
		 * 
		 * public boolean onPreferenceClick(Preference preference) {
		 * Toast.makeText(getBaseContext(),
		 * "The custom preference has been clicked", Toast.LENGTH_LONG).show();
		 * SharedPreferences customSharedPreference = getSharedPreferences(
		 * "myCustomSharedPrefs", Activity.MODE_PRIVATE);
		 * SharedPreferences.Editor editor = customSharedPreference .edit();
		 * editor.putString("myCustomPref", "The preference has been clicked");
		 * editor.commit(); return true; } });
		 */
		/*
		 * Button prefBtn = (Button) findViewById(R.id.prefButton);
		 * prefBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent settingsActivity = new
		 * Intent(getBaseContext(), Preferences.class);
		 * startActivity(settingsActivity); } });
		 */
	}

	public void onBackPressed() {
		//idan.delete_sync = preferences.getBoolean("delete_sync", false);
		setResult(555);
		finish();
	}

	private void getPrefs() {
		boolean delete_sync;
		String ListPreference;
		String editTextPreference;
		String ringtonePreference;
		String secondEditTextPreference;
		String customPref;
		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		delete_sync = prefs.getBoolean("delete_sync", false);
		ListPreference = prefs.getString("listPref", "nr1");
		editTextPreference = prefs.getString("editTextPref",
				"Nothing has been entered");
		ringtonePreference = prefs.getString("ringtonePref",
				"DEFAULT_RINGTONE_URI");
		secondEditTextPreference = prefs.getString("SecondEditTextPref",
				"Nothing has been entered");
		// Get the custom preference
		SharedPreferences mySharedPreferences = getSharedPreferences(
				"myCustomSharedPrefs", Activity.MODE_PRIVATE);
		customPref = mySharedPreferences.getString("myCusomPref", "");
	}

	/*
	 * public void onStart() { // Bundle savedInstanceState // public void
	 * onStart() { boolean CheckboxPreference; String ListPreference; String
	 * editTextPreference; String ringtonePreference; String
	 * secondEditTextPreference; String customPref; super.onStart();
	 * getPreferenceManager().setSharedPreferencesName();
	 */

	/*
	 * public void getPrefs() { // Get the xml/preferences.xml preferences
	 * SharedPreferences prefs = PreferenceManager
	 * .getDefaultSharedPreferences(getBaseContext()); CheckboxPreference =
	 * prefs.getBoolean("checkboxPref", true); ListPreference =
	 * prefs.getString("listPref", "nr1"); editTextPreference =
	 * prefs.getString("editTextPref", "Nothing has been entered");
	 * ringtonePreference = prefs.getString("ringtonePref",
	 * "DEFAULT_RINGTONE_URI"); secondEditTextPreference =
	 * prefs.getString("SecondEditTextPref", "Nothing has been entered"); // Get
	 * the custom preference SharedPreferences mySharedPreferences =
	 * getSharedPreferences( "myCustomSharedPrefs", Activity.MODE_PRIVATE);
	 * customPref = mySharedPreferences.getString("myCusomPref", ""); }
	 */
	// }
}

/*
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * addPreferencesFromResource(R.xml.preferences); // Get the custom preference
 * Preference customPref = (Preference) findPreference("customPref"); customPref
 * .setOnPreferenceClickListener(new OnPreferenceClickListener() {
 * 
 * public boolean onPreferenceClick(Preference preference) {
 * Toast.makeText(getBaseContext(), "The custom preference has been clicked",
 * Toast.LENGTH_LONG).show(); SharedPreferences customSharedPreference =
 * getSharedPreferences( "myCustomSharedPrefs", Activity.MODE_PRIVATE);
 * SharedPreferences.Editor editor = customSharedPreference .edit();
 * editor.putString("myCustomPref", "The preference has been clicked");
 * editor.commit(); return true; }
 * 
 * }); } }
 */
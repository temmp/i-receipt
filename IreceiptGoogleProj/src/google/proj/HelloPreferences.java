package google.proj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class HelloPreferences extends Activity {
	SharedPreferences preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsmain);
		Button button = (Button) findViewById(R.id.Button01);
		// Initialize preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String username = preferences.getString("username", "n/a");
				String password = preferences.getString("password", "n/a");
			}
		});

		Button buttonChangePerferences = (Button) findViewById(R.id.Button02);
		buttonChangePerferences.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor edit = preferences.edit();
				String username = preferences.getString("username", "n/a");
				// We will just revert the current user name and save again
				/*
				 * StringBuffer buffer = new StringBuffer(); for (int i =
				 * username.length() - 1; i >= 0; i--) {
				 * buffer.append(username.charAt(i)); }
				 */
				edit.putString("username", username);
				edit.commit();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * switch (item.getItemId()) { case R.id.preferences:
		 */
		// Launch Preference activity
		Intent i = new Intent(HelloPreferences.this, Preferences.class);
		startActivity(i);
		return true;
	}
}
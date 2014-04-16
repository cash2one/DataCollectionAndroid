package main;

import java.io.File;

import makeUser.MakeUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
	/*
	 * Gets tokens, saves phone number and starts a alarm that will run sms/mms
	 * service once a day.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File f = new File(
				"/data/data/edu.uiowa.datacollection.sms/shared_prefs/mypref.xml");
		if (f.exists()) {
			// Is registered
		} else {
			// needs to register
			Intent intent = new Intent(this, MakeUser.class);
			startActivity(intent);
		}
	}
}

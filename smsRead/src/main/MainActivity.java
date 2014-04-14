package main;

import makeUser.MakeUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	/*
	 * Gets tokens, saves phone number and starts a alarm that will run sms/mms
	 * service once a day.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, MakeUser.class);
		startActivity(intent);
	}
}

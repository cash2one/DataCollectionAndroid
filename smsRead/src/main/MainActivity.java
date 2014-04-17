package main;

import makeUser.MakeUser;
import postRegistration.SecondaryActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity
{
	/*
	 * Gets tokens, saves phone number and starts a alarm that will run sms/mms
	 * service once a day.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPref = this.getApplicationContext()
				.getSharedPreferences("mypref", 0);
		if (sharedPref.getString("phone_number", null) != null)
		{
			// Is registered
			Intent intent = new Intent(this, SecondaryActivity.class);
			startActivity(intent);
			finish();
		}
		else
		{
			// needs to register
			Intent intent = new Intent(this, MakeUser.class);
			startActivity(intent);
			finish();
		}
	}
}

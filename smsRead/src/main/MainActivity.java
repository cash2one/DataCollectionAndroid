package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import post_registration.SecondaryActivity;
import registration.MakeUser;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

public class MainActivity extends Activity
{
	public static final String BASE_URL = "http://128.255.227.134:7777/DataCollection";
	public static final String ANDROID_UPLOAD_URL = BASE_URL + "/postandroid/";
	public static final String POST_TOKEN_URL = BASE_URL + "/newtoken/";
	public static final String SURVEY_URL = BASE_URL + "/survey/"; 
	public static final String CREATE_USER_URL = BASE_URL + "/makeuser/";
	public static final String GET_HELP_URL = BASE_URL + "/gethelp/";
	public static final String REPORT_BULLYING_URL = BASE_URL
			+ "/reportbullying/";
	public static final String WITHDRAW_REQUEST_URL = BASE_URL + "/withdraw/";

	/*
	 * Gets tokens, saves phone number and starts a alarm that will run sms/mms
	 * service once a day.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Call this method if there is an authentication problem, it is only
		// needed the first time getting the app authenticated with facebook,
		// and remains for debugging purposes.
		getDebugKeyhash();

		SharedPreferences sharedPref = this.getApplicationContext()
				.getSharedPreferences("mypref", 0);
		if ((sharedPref.getString("phone_number", null) != null))
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

	/**
	 * This method fixes some app authentication errors when run for the first
	 * time before the app is published.
	 */
	private void getDebugKeyhash()
	{
		PackageInfo info = null;
		try
		{
			info = getPackageManager().getPackageInfo(
					"edu.uiowa.datacollection.sms",
					PackageManager.GET_SIGNATURES);
		}
		catch (NameNotFoundException e1)
		{
			Log.i("ERROR:", "Couldn't make info");
		}

		for (Signature signature : info.signatures)
		{
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("SHA");
			}
			catch (NoSuchAlgorithmException e)
			{
				Log.i("ERROR:", "Couldn't make md");
			}
			md.update(signature.toByteArray());
			Log.i("KeyHash for Facebook:",
					Base64.encodeToString(md.digest(), Base64.DEFAULT));
		}
	}
}

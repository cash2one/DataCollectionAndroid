package registration;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;

import main.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import utilities.DialogUtilities;
import alarmreceiver.AlarmReceiver;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadRegistration extends AsyncTask<JSONObject, Void, String>
{
	public static final String PASS_RESULT = "PASS";
	public static final String FAIL_RESULT = "FAIL";

	public static final String FAIL_MESSAGE = "Sorry, it seems like our server"
			+ " is experiencing some difficulties or "
			+ "you aren't connected to the internet.\n"
			+ "If your internet connection isn't working "
			+ "try again when you have an internet " + "connection.\n"
			+ "If your internet is working, please wait "
			+ "24 hours to allow server maintenance.\n"
			+ "Thank you for your patience.";
	public static String FAIL_TITLE = "Registration error";

	public static String ERROR_MESSAGE = "An unknown error has occured with"
			+ " your registration. This error occured on "
			+ "our server. Please try again in " + "24 hours. "
			+ "Thank you for your patience.";
	public static String ERROR_TITLE = "Registration error";

	private Activity context;
	private String phoneNumber;

	public UploadRegistration(Activity context, String phoneNumber)
	{
		this.context = context;
		this.phoneNumber = phoneNumber;
	}

	protected String doInBackground(JSONObject... params)
	{
		HttpPost post = new HttpPost(MainActivity.CREATE_USER_URL);
		post.setEntity(new ByteArrayEntity(params[0].toString().getBytes()));
		HttpResponse resp = null;
		HttpClient httpclient = new DefaultHttpClient();
		try
		{
			resp = httpclient.execute(post);
			String result = null;
			result = EntityUtils.toString(resp.getEntity());
			return result;
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// The connection to the server was refused
			if (e instanceof ConnectException)
			{
				return FAIL_RESULT;
			}
			else
			{
				e.printStackTrace();
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This is run once the upload has finished. If the pass result is returned
	 * then we save the registration data and say thank you. Otherwise it says
	 * failed and exits.
	 */
	protected void onPostExecute(String result)
	{
		System.out.println(result);
		if (result.equals(PASS_RESULT))
		{
			Toast.makeText(context, "Thank you!", Toast.LENGTH_LONG).show();

			/*
			 * Author max starts Alarm, saves phone number, and sets a default
			 * date to get all messages
			 */
			savePhoneNumber();
			AlarmReceiver alarm = new AlarmReceiver();
			alarm.setAlarm(context.getApplicationContext());

			// Exit the activity
			context.finish();

		}
		else if (result.equals(FAIL_RESULT))
		{
			boolean closeOnExit = true;
			DialogUtilities.createInfoDialogWithExitButton(FAIL_MESSAGE,
					FAIL_TITLE, closeOnExit, context);
		}
		else
		{

			boolean closeOnExit = true;
			DialogUtilities.createInfoDialogWithExitButton(ERROR_MESSAGE,
					ERROR_TITLE, closeOnExit, context);
		}
	}

	/**
	 * Saves the phone number, the last uploaded date, and the tokenAge so that
	 * the Alarm knows the data and can use it.
	 */
	protected void savePhoneNumber()
	{
		// Author Max
		// Create object of SharedPreferences for the save users number
		// and a default date

		SharedPreferences sharedPref = context.getApplicationContext()
				.getSharedPreferences("mypref", 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("phone_number", phoneNumber);
		// jan 1, 1970 a default to get all messages
		Date date = new Date(0);
		editor.putLong("lastUploaded", date.getTime());
		date = new Date();
		editor.putLong("tokenAge", date.getTime());
		// commits to save
		editor.commit();
	}
}

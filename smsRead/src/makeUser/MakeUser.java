package makeUser;

import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import alarmreceiver.AlarmReceiver;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;

import edu.uiowa.datacollection.sms.R;

/**
 * The purpose of this class is to handle the main operations of the app as well
 * as the user interface and authenticating with Facebook. It handles user input
 * and collects data with a {@link datacollection.DataManager}.
 * 
 * @author Tom
 */
public class MakeUser extends Activity
{

	private static final String SERVER_URL = "http://172.23.6.179:8001/DataCollection/makeuser/";
	public static final String PASS_RESULT = "PASS";
	public static final String FAIL_RESULT = "FAIL";

	private Button loginToFacebook;
	private Button loginToTwitter;
	private String oauthText = "";
	private String oauthSecretText = "";
	private String screenNameText = "";
	private Button done;
	private SharedPreferences sharedPreferences;
	private EditText phoneField;
	private TextView phoneLabel;
	private String twitterID;
	private Button helpButton;
	private boolean facebookSkipped;
	private boolean twitterSkipped;

	/**
	 * This method initializes all of the pieces of the app - the dataManager,
	 * the Facebook session, and the user interface.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("test", "makeUser");
		// Create the user interface
		setupUI();

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (!sharedPreferences.getBoolean(
				ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, false))
		{
			initControl();
		}

	}

	/**
	 * All of the app's UI initialization goes here, it also resets all of the
	 * UI elements
	 */
	private void setupUI()
	{
		loginToFacebook = (Button) findViewById(R.id.loginToFacebookButton);
		loginToFacebook.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				openFacebookDialog();
			}
		});

		loginToTwitter = (Button) findViewById(R.id.loginToTwitterButton);
		loginToTwitter.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.remove(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN);
				editor.remove(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET);
				editor.remove(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN);
				editor.commit();
				openTwitterDialog();
			}
		});

		done = (Button) findViewById(R.id.doneButton);
		done.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				uploadData();
			}
		});

		helpButton = (Button) findViewById(R.id.helpButton);
		helpButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				createHelpDialog();
			}
		});

		phoneField = (EditText) findViewById(R.id.phoneInput);
		phoneLabel = (TextView) findViewById(R.id.phoneLabel);
	}

	protected void openFacebookDialog()
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Facebook Login");

		Drawable myIcon = getResources().getDrawable(R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(getResources(), bitmapResized);

		builder.setIcon(icon);

		String message = "As part of the study you are required to log into Facebook."
				+ "\nIf you do not have a Facebook, press 'Skip'";
		builder.setMessage(message);
		builder.setPositiveButton("Login",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						openFacebookSession();
					}
				});
		builder.setNegativeButton("Skip", new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{
				loginToFacebook.setEnabled(false);
				loginToTwitter.setEnabled(true);
				facebookSkipped = true;
			}
		});

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	protected void openTwitterDialog()
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Twitter Login");

		Drawable myIcon = getResources().getDrawable(R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(getResources(), bitmapResized);

		builder.setIcon(icon);

		String message = "As part of the study you are required to log into Twitter."
				+ "\nIf you do not have a Twitter, press 'Skip'";
		builder.setMessage(message);
		builder.setPositiveButton("Login",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						openTwitterSession();
					}
				});
		builder.setNegativeButton("Skip", new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{
				loginToFacebook.setEnabled(false);
				loginToTwitter.setEnabled(false);
				phoneField.setEnabled(true);
				phoneLabel.setEnabled(true);
				twitterSkipped = true;
			}
		});

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	protected void createHelpDialog()
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Registration help");

		Drawable myIcon = getResources().getDrawable(R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(getResources(), bitmapResized);

		builder.setIcon(icon);

		String message = "Registration steps:"
				+ "\n\t1) Press the Facebook button and login."
				+ "\n\t2) Press the Twitter button and login."
				+ "\n\t3) Enter your phone number."
				+ "\n\t4) Press submit."
				+ "\nQuestions or comments? Contact support@uiowa.cyberbullying.edu";
		builder.setMessage(message);
		builder.setPositiveButton("Exit", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	protected void savePhoneNumber()
	{
		// Author Max
		// Create object of SharedPreferences for the save users number
		// and a default date

		SharedPreferences sharedPref = getApplicationContext()
				.getSharedPreferences("mypref", 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("phone_number", phoneField.getText().toString());
		// jan 1, 1970 a default to get all messages
		Date date = new Date(0);
		editor.putLong("lastUploaded", date.getTime());
		date = new Date();
		editor.putLong("tokenAge", date.getTime());
		// commits to save
		editor.commit();
	}

	private void uploadData()
	{

		boolean twitterFinished = !(oauthText.length() == 0
				|| oauthSecretText.length() == 0 || screenNameText.length() == 0)
				|| twitterSkipped == true;
		boolean facebookFinished = (Session.getActiveSession() != null || facebookSkipped == true)
				|| twitterFinished;

		if (twitterFinished && (Session.getActiveSession() == null))
			facebookSkipped = true;

		boolean phoneFinished = !(phoneField.getText() == null
				|| phoneField.getText().toString().length() == 0 || phoneField
				.getText().toString().length() < 10);

		if (!facebookFinished || !twitterFinished || !phoneFinished)
		{
			if (!facebookFinished)
				Toast.makeText(this, "Complete Facebook login",
						Toast.LENGTH_LONG).show();
			if (!twitterFinished)
				Toast.makeText(this, "Complete Twitter login",
						Toast.LENGTH_LONG).show();
			if (!phoneFinished)
				Toast.makeText(this, "Complete Phone Number", Toast.LENGTH_LONG)
						.show();
			return;
		}

		JSONObject obj = new JSONObject();
		try
		{
			obj.put("phone_number", phoneField.getText().toString());

			String token = "";
			String appId = "";
			if (!facebookSkipped)
			{
				token = Session.getActiveSession().getAccessToken();
				appId = Session.getActiveSession().getApplicationId();
			}

			obj.put("facebook_token", token);
			obj.put("facebook_appid", appId);

			obj.put("twitter_token", this.oauthText);
			obj.put("twitter_secret", this.oauthSecretText);
			obj.put("twitter_screen_name", this.screenNameText);
			obj.put("twitter_id", twitterID);
			System.out.println(obj.toString(1));

			AsyncTask<JSONObject, Void, String> postData = new AsyncTask<JSONObject, Void, String>()
			{
				protected String doInBackground(JSONObject... params)
				{
					HttpPost post = new HttpPost(SERVER_URL);
					post.setEntity(new ByteArrayEntity(params[0].toString()
							.getBytes()));
					HttpResponse resp = null;
					HttpClient httpclient = new DefaultHttpClient();
					try
					{
						resp = httpclient.execute(post);
						String result = null;
						try
						{
							result = EntityUtils.toString(resp.getEntity());

						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						return result;
					}
					catch (ClientProtocolException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					return null;
				}

				protected void onPostExecute(String result)
				{
					System.out.println(result);
					if (result.equals(PASS_RESULT))
					{
						Toast.makeText(MakeUser.this, "Thank you!",
								Toast.LENGTH_LONG).show();

						/*
						 * Author max starts Alarm, saves phone number, and sets
						 * a default date to get all messages
						 */
						savePhoneNumber();
						AlarmReceiver alarm = new AlarmReceiver();
						alarm.setAlarm(getApplicationContext());

					}
					else
					{
						Toast.makeText(MakeUser.this, "Failed upload",
								Toast.LENGTH_LONG).show();
					}
				}

			};

			postData.execute(obj);
			finish();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}

	}

	private void openTwitterSession()
	{

		new TwitterAuthenticateTask().execute();
	}

	/**
	 * This method sets up our facebook connection.
	 */
	private void openFacebookSession()
	{
		// Call this method if there is an authentication problem, it was only
		// needed the first time getting the app authenticated with facebook,
		// and remains for debugging purposes.
		getKeyIfKeyWrong();

		// start Facebook Login
		Session.openActiveSession(this, true, new Session.StatusCallback()
		{
			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception)
			{
				if (session.isOpened())
				{
					ArrayList<String> permissions = new ArrayList<String>();
					permissions.add("read_mailbox");
					permissions.add("read_stream");

					// Remove callback from old session to prevent infinite loop
					session.removeCallback(this);

					// send our permissions request
					Session.getActiveSession().requestNewReadPermissions(
							new NewPermissionsRequest(MakeUser.this,
									permissions));
					loginToFacebook.setEnabled(false);
					loginToTwitter.setEnabled(true);
				}
			}
		});
	}

	/**
	 * This method fixes some app authentication errors when run for the first
	 * time before the app is published.
	 */
	private void getKeyIfKeyWrong()
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
			Log.i("KeyHash:",
					Base64.encodeToString(md.digest(), Base64.DEFAULT));
		}
	}

	private void initControl()
	{
		Uri uri = getIntent().getData();
		if (uri != null
				&& uri.toString().startsWith(
						ConstantValues.TWITTER_CALLBACK_URL))
		{
			String verifier = uri
					.getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
			TwitterGetAccessTokenTask t = new TwitterGetAccessTokenTask();
			t.execute(verifier);

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	
	class TwitterAuthenticateTask extends
			AsyncTask<String, String, RequestToken>
	{

		@Override
		protected void onPostExecute(RequestToken requestToken)
		{
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(requestToken.getAuthenticationURL()));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			startActivity(intent);
			finish();
		}

		@Override
		protected RequestToken doInBackground(String... params)
		{
			return TwitterUtil.getInstance().getRequestToken();
		}
	}
	

	class TwitterGetAccessTokenTask extends
			AsyncTask<String, String, AccessToken>
	{

		@Override
		protected void onPostExecute(AccessToken accessToken)
		{
			if (accessToken == null)
			{
				oauthText = "";
				oauthSecretText = "";
				screenNameText = "";
				twitterID = "";

				System.out.println("Error with first attempt, trying again.");
				openTwitterSession();
			}
			else
			{
				oauthText = (accessToken.getToken());
				oauthSecretText = (accessToken.getTokenSecret());
				screenNameText = (accessToken.getScreenName());
				twitterID = "" + accessToken.getUserId();

				loginToFacebook.setEnabled(false);
				loginToTwitter.setEnabled(false);
				phoneField.setEnabled(true);
				phoneLabel.setEnabled(true);
			}
		}

		@Override
		protected AccessToken doInBackground(String... params)
		{
			AccessToken accessToken = TwitterUtil.getInstance().getAccessToken(
					params[0]);
			accessToken = TwitterUtil.getInstance().getAccessToken(params[0]);
			if (accessToken == null)
			{
				return null;
			}
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN,
					accessToken.getToken());
			editor.putString(
					ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
					accessToken.getTokenSecret());
			// editor.putBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,
			// true);
			editor.commit();
			return accessToken;
		}
	}

	
}
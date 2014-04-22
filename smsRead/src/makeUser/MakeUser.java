package makeUser;

import java.io.IOException;
import java.net.ConnectException;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

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

	private static final String SERVER_URL = "http://128.255.45.52:7777/DataCollection/makeuser/";
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

		// Create the user interface
		setupUI();

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (!sharedPreferences.getBoolean(
				ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, false))
		{
			accessTwitterAccessTokenPostAuthentication();
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
				String message = "Registration steps:"
						+ "\n\t1) Press the Facebook button and login."
						+ "\n\t2) Press the Twitter button and login."
						+ "\n\t3) Enter your phone number."
						+ "\n\t4) Press submit."
						+ "\nQuestions or comments? Contact support@uiowa.cyberbullying.edu";
				String title = "Registration help";
				boolean closeOnExit = false;
				createInfoDialogWithExitButton(message, title, closeOnExit);
			}
		});

		phoneField = (EditText) findViewById(R.id.phoneInput);
		phoneLabel = (TextView) findViewById(R.id.phoneLabel);
	}

	/**
	 * Creates the Skip or Login dialog for Facebook
	 */
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
				loginToFacebook.setText("Skipped Facebook login");
				loginToTwitter.setEnabled(true);
				facebookSkipped = true;
			}
		});

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	/**
	 * Creates the Skip or Login dialog for Twitter
	 */
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
				loginToTwitter.setText("Skipped Twitter login");
			}
		});

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	/**
	 * Creates the Help dialog
	 * @param closeOnExit 
	 */
	protected void createInfoDialogWithExitButton(String text, String title,
			final boolean closeOnExit)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);

		Drawable myIcon = getResources().getDrawable(R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(getResources(), bitmapResized);

		builder.setIcon(icon);

		builder.setMessage(text);
		builder.setPositiveButton("Exit", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (closeOnExit)
					finish();
			}
		});
		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
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

	/**
	 * This method uploads all of the registration data to the server
	 */
	private void uploadData()
	{
		// Checks to see if the parts are finished

		boolean twitterFinished = !(oauthText.length() == 0
				|| oauthSecretText.length() == 0 || screenNameText.length() == 0)
				|| twitterSkipped == true;
		boolean facebookFinished = (Session.getActiveSession() != null || facebookSkipped == true)
				|| twitterFinished;

		// If twitter has finished we know facebook has to be finished, and
		// if active session is null then we know they had to skip it
		if (twitterFinished && (Session.getActiveSession() == null))
			facebookSkipped = true;

		boolean phoneFinished = !(phoneField.getText() == null
				|| phoneField.getText().toString().length() == 0 || phoneField
				.getText().toString().length() < 10);

		// If they haven't finished then tell them what to do and don't upload
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
						result = EntityUtils.toString(resp.getEntity());
						return result;
					}
					catch (ClientProtocolException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						//The connection to the server was refused
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
				 * This is run once the upload has finished. If the pass result
				 * is returned then we save the registration data and say thank
				 * you. Otherwise it says failed and exits.
				 */
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

						// Exit the activity
						finish();

					}
					else if (result.equals(FAIL_RESULT))
					{
						String message = "Sorry, it seems like our server"
								+ " is experiencing some difficulties or "
								+ "you aren't connected to the internet.\n"
								+ "If your internet connection isn't working "
								+ "try again when you have an internet "
								+ "connection.\n"
								+ "If your internet is working, please wait "
								+ "24 hours to allow server maintenance.\n"
								+ "Thank you for your patience.";
						String title = "Registration error";
						boolean closeOnExit = true;
						createInfoDialogWithExitButton(message, title, closeOnExit);
					}
					else
					{
						String message = "An unknown error has occured with"
								+ " your registration. Please try again in "
								+ "24 hours. "
								+ "Thank you for your patience.";
						String title = "Registration error";
						boolean closeOnExit = true;
						createInfoDialogWithExitButton(message, title, closeOnExit);
					}
				}

			};

			// Start the upload
			postData.execute(obj);
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

	/**
	 * Starts the twitter opening sequence
	 */
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

					updateFacebookButton();
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

	/**
	 * This method is called when the focus of of the app is returned to this
	 * app, as opposed to the web browser.
	 */
	private void accessTwitterAccessTokenPostAuthentication()
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

	/**
	 * This class is just a default Activity class, don't change it, it does
	 * something wtih facebook.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	/**
	 * Internal class used for opening the browser for twitter authentication
	 * 
	 */
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

	/**
	 * Internal class used for accessing the access token after we have finished
	 * authentication
	 */
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
				loginToTwitter.setText("Logged in as " + screenNameText);
				phoneField.setEnabled(true);
				phoneLabel.setEnabled(true);

				updateFacebookButton();
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

	public void updateFacebookButton()
	{
		Log.i("here", "here");
		if (Session.getActiveSession() == null)
		{
			loginToFacebook.setText("Skipped Facebook login");
		}
		else
		{
			Log.i("here", "here");
			Request.newMeRequest(Session.getActiveSession(),
					new Request.GraphUserCallback()
					{
						// callback after Graph API response with user
						// object
						@Override
						public void onCompleted(GraphUser user,
								Response response)
						{
							loginToFacebook.setText("Logged in as "
									+ user.getName());
						}
					}).executeAsync();
		}
	}

}
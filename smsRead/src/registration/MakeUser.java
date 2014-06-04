package registration;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import utilities.InterfaceUtilities;
import utilities.InternetUtilities;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private String facebookID;

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

		// This gets called when the app focus returns to our app after
		// getting the tiwtter data.
		if (!sharedPreferences.getBoolean(
				TwitterUtilities.PREFERENCE_TWITTER_IS_LOGGED_IN, false))
		{
			accessTwitterAccessTokenPostAuthentication();
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
				InterfaceUtilities.createHelpDialog(MakeUser.this);
			}
		});

		phoneField = (EditText) findViewById(R.id.phoneInput);
		phoneLabel = (TextView) findViewById(R.id.phoneLabel);
	}

	protected void openTwitterDialog()
	{
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(TwitterUtilities.PREFERENCE_TWITTER_OAUTH_TOKEN);
		editor.remove(TwitterUtilities.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET);
		editor.remove(TwitterUtilities.PREFERENCE_TWITTER_IS_LOGGED_IN);
		editor.commit();
		String message = "As part of the study you are required to log into Twitter."
				+ "\nIf you do not have a Twitter, press 'Skip'";

		InterfaceUtilities.createLoginDialog(MakeUser.this, "Login",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						openTwitterSession();
					}
				}, "Skip", new DialogInterface.OnClickListener()
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
				}, message, "Login to Twitter");
	}

	/**
	 * Creates the Skip or Login dialog for Facebook
	 */
	protected void openFacebookDialog()
	{
		String message = "As part of the study you are required to log into Facebook."
				+ "\nIf you do not have a Facebook, press 'Skip'";
		InterfaceUtilities.createLoginDialog(MakeUser.this, "Login",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						openFacebookSession();
					}
				}, "Skip", new DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface dialog, int which)
					{
						loginToFacebook.setEnabled(false);
						loginToFacebook.setText("Skipped Facebook login");
						loginToTwitter.setEnabled(true);
						facebookSkipped = true;
					}
				}, message, "Facebook Login");
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
		
		Toast.makeText(MakeUser.this,
				"Uploading data. This may take a few seconds...",
				Toast.LENGTH_LONG).show();
		done.setEnabled(false);

		// We've passed all of the checks, everything seems to be in order.
		// Lets upload!
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
			obj.put("facebook_id", facebookID);

			obj.put("twitter_token", this.oauthText);
			obj.put("twitter_secret", this.oauthSecretText);
			obj.put("twitter_screen_name", this.screenNameText);
			obj.put("twitter_id", twitterID);
			System.out.println(obj.toString(1));

			UploadRegistration postData = new UploadRegistration(this,
					phoneField.getText().toString(), facebookSkipped,token);
			System.out.println("Beginning upload");
			// Start the upload
			postData.execute(obj);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Starts the twitter opening sequence
	 */
	public void openTwitterSession()
	{
		Toast.makeText(this, "Opening Twitter, this may take a second",
				Toast.LENGTH_LONG).show();
		new OpenTwitterAuthentication(this).execute();
	}

	/**
	 * This method sets up our facebook connection.
	 */
	private void openFacebookSession()
	{
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
					
					if (!InternetUtilities.hasFacebookAccess())
						return;
					
					
					updateFacebookButton();
					loginToFacebook.setEnabled(false);
					loginToTwitter.setEnabled(true);

					
				}
			}
		});
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
						TwitterUtilities.TWITTER_CALLBACK_URL))
		{
			String verifier = uri
					.getQueryParameter(TwitterUtilities.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
			new GetTwitterAccessToken(this).execute(verifier);

		}
	}

	public void updateFacebookButton()
	{
		if (Session.getActiveSession() == null)
		{
			loginToFacebook.setText("Skipped Facebook login");
		}
		else
		{
			Request.newMeRequest(Session.getActiveSession(),
					new Request.GraphUserCallback()
					{
						// callback after Graph API response with user
						// object
						@Override
						public void onCompleted(GraphUser user,
								Response response)
						{
							if (user != null)
							{
								loginToFacebook.setText("Logged in as "
										+ user.getName());
								facebookID = user.getId();
							}
						}
					}).executeAsync();
		}
	}

	/*
	 * GETTERS AND SETTERS
	 */

	public Button getLoginToFacebook()
	{
		return loginToFacebook;
	}

	public void setLoginToFacebook(Button loginToFacebook)
	{
		this.loginToFacebook = loginToFacebook;
	}

	public Button getLoginToTwitter()
	{
		return loginToTwitter;
	}

	public void setLoginToTwitter(Button loginToTwitter)
	{
		this.loginToTwitter = loginToTwitter;
	}

	public boolean isFacebookSkipped()
	{
		return facebookSkipped;
	}

	public void setFacebookSkipped(boolean facebookSkipped)
	{
		this.facebookSkipped = facebookSkipped;
	}

	public SharedPreferences getSharedPreferences()
	{
		return sharedPreferences;
	}

	public void setOauthText(String oauthText)
	{
		this.oauthText = oauthText;
	}

	public void setOauthSecretText(String oauthSecretText)
	{
		this.oauthSecretText = oauthSecretText;
	}

	public void setScreenNameText(String screenNameText)
	{
		this.screenNameText = screenNameText;
	}

	public void setTwitterID(String twitterID)
	{
		this.twitterID = twitterID;
	}

	public void setTwitterSkipped(boolean twitterSkipped)
	{
		this.twitterSkipped = twitterSkipped;
	}

	public EditText getPhoneField()
	{
		return phoneField;
	}

	public TextView getPhoneLabel()
	{
		return phoneLabel;
	}
	
	public String getFacebookID()
	{
		return facebookID;
	}

}
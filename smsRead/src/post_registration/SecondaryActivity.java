package post_registration;

import java.util.Date;

import main.MainActivity;
import registration.MakeUser;
import sms_messages.User;
import utilities.InterfaceUtilities;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import edu.uiowa.datacollection.sms.R;

public class SecondaryActivity extends Activity
{
	private Button reRegisterButton;
	private Button reOpenSurvey;
	private Button getHelpButton;
	private Button reportButton;
	private Button helpButton;
	private TextView uploadTimeText;
	private SharedPreferences sharedPreferences;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(SecondaryActivity.this, "7b31e3a2");
		setContentView(R.layout.post_registration_layout);

		// Create the user interface
		setupUI();

	}

	private void setupUI()
	{
		reRegisterButton = (Button) findViewById(R.id.reregisterButton);
		reRegisterButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				reRegister();
			}
		});

		reOpenSurvey = (Button) findViewById(R.id.launchSurveyButton);
		reOpenSurvey.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				launchSurvey();
			}
		});

		getHelpButton = (Button) findViewById(R.id.getHelpButton);
		getHelpButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				getHelp();
			}
		});

		reportButton = (Button) findViewById(R.id.reportBullyingButton);
		reportButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				report();
			}
		});

		helpButton = (Button) findViewById(R.id.helpButton);
		helpButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				InterfaceUtilities.createHelpDialog(SecondaryActivity.this);
			}
		});

		uploadTimeText = (TextView) findViewById(R.id.lastUploadText);

		User user = new User(getApplicationContext());
		Date lastFail = user.getLastFail();
		Date lastUpload = user.getDate();

		if (lastUpload.after(lastFail))
		{
			uploadTimeText.setText("Successfully uploaded: "
					+ lastUpload.toString());
		}
		else if (lastFail.getTime() == 0)
		{
			uploadTimeText.setText("No uploads yet.");
		}
		else
		{
			uploadTimeText.setText("Could not upload: " + lastFail.toString());
		}
	}

	protected void getHelp()
	{
		Uri uri = Uri.parse(MainActivity.GET_HELP_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	protected void report()
	{
		Uri uri = Uri.parse(MainActivity.REPORT_BULLYING_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	protected void launchSurvey()
	{
		new CheckForSurveys(this).execute();
		Toast.makeText(this, "Checking for surveys", Toast.LENGTH_SHORT).show();
	}

	protected void reRegister()
	{	
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
		Intent intent = new Intent(this, MakeUser.class);
		startActivity(intent);
	}
}

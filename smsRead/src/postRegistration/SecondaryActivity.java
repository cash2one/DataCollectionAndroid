package postRegistration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

import makeUser.MakeUser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import androidMessages.Upload;
import androidMessages.User;
import edu.uiowa.datacollection.sms.R;

public class SecondaryActivity extends Activity
{
	private Button reRegisterButton;
	private Button reOpenSurvey;
	private Button getHelpButton;
	private Button reportButton;
	private static String HELP_URL = "http://172.23.6.179:8001/DataCollection/servey/";
	private static String REPORT_URL = "http://172.23.6.179:8001/DataCollection/servey/";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
	}

	protected void getHelp()
	{
		Uri uri = Uri.parse(HELP_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}
	
	protected void report()
	{
		Uri uri = Uri.parse(REPORT_URL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	protected void launchSurvey()
	{
		final User theUser = new User(this);
		final Upload upload = new Upload();
		final Context context = this;
		AsyncTask<Void, Void, Boolean> notOnMainThread = new AsyncTask<Void, Void, Boolean>()
		{
			@Override
			protected Boolean doInBackground(Void... params)
			{
				return upload.checkForServey(context, theUser);
			}
			
		};
		try
		{
			if (!notOnMainThread.execute().get())
			{
				Toast.makeText(context,
						"Thanks for checking but you've finished all the surveys!",
						Toast.LENGTH_LONG).show();
			}
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void reRegister()
	{
		Intent intent = new Intent(this, MakeUser.class);
		startActivity(intent);
	}
}

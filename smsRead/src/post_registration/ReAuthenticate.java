package post_registration;

import java.util.ArrayList;

import main.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;


/**
 * This class is used so that the access token for Facebook can be renewed.
 * When it is created it does the facebook authentication.
 * 
 * 
 */
public class ReAuthenticate extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (Session.getActiveSession() != null && Session.getActiveSession().getState() == SessionState.OPENED)
		{
			System.out.println("Here");
			Intent newFaceBookToken = new Intent(this, MainActivity.class);
			newFaceBookToken.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(newFaceBookToken);
		}
		
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
							new NewPermissionsRequest(ReAuthenticate.this,
									permissions));
					
					// get phone number from intent extras
					Bundle extras = getIntent().getExtras();
					String number = null;
					if (extras != null)
						number = extras.getString("phone_number");
					
					Intent intent = new Intent(ReAuthenticate.this, PostTokenIntent.class);
					intent.putExtra("phone_number", number);
					intent.putExtra("token", Session.getActiveSession().getAccessToken());
					startService(intent);
					finish();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
		
	}
}

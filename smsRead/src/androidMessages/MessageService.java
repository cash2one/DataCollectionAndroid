package androidMessages;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import postRegistration.SecondaryActivity;

import edu.uiowa.datacollection.sms.R;
import postRegistration.ReAuthenticate;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * MessageService is an intentService which will run message collection and
 * uploading on its on worker thread.
 * 
 */
public class MessageService extends IntentService
{
	public MessageService()
	{
		super("MessageService");
	}

	protected void onHandleIntent(Intent intent)
	{
		Sms sms = new Sms();
		Mms mms = new Mms();
		User theUser = new User(this);
		ConstructConversation conversationConstruct = new ConstructConversation();
		List<MessageU> smsMessages = sms.getSms(this, theUser);// list of all
																// sms messages
		List<MessageU> mmsMessages = mms.getMms(this, theUser);// list of all
																// mms messages
		smsMessages.addAll(mmsMessages);// list of all messages
		List<Conversation> conversationList = conversationConstruct
				.construct(smsMessages);// messages to conversations
		JSONArray arr = new JSONArray();
		for (Conversation conversation : conversationList)
		{
			arr.put(conversation.toJson());// conversations to json
		}
		JSONObject uploadData = new JSONObject();
		try
		{
			uploadData.put("conversation", arr);
			uploadData.put("user", theUser.getUser());
		}
		catch (JSONException e1)
		{
			Log.i("json", "json Exception");
			e1.printStackTrace();
		}

		Upload upload = new Upload(uploadData);
		String post = upload.post();// json to server
		if (post.equals("worked"))
		{
			Date date = new Date();
			theUser.setDate(date);
		}
		if (upload.checkForServey(this, theUser))
		{
			createNotification();
			Log.i("HERE!!", "CMON");
		}

		Log.i("SDF", "SDFS");

		Intent newFaceBookToken = new Intent(this, ReAuthenticate.class);
		newFaceBookToken.putExtra("phone_number", theUser.getUser());
		newFaceBookToken.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(newFaceBookToken);

	}

	// Tom
	private void createNotification()
	{
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.notif_icon)
				.setContentTitle("Survey")
				.setContentText("Please complete the survey");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, SecondaryActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SecondaryActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}
}

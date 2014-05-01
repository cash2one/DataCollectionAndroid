package sms_messages;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import post_registration.ReAuthenticate;
import registration.UploadRegistration;
import utilities.InterfaceUtilities;
import utilities.ServerUtilities;
import android.app.IntentService;
import android.content.Intent;
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
		Log.i("data",uploadData.toString());
		Upload upload = new Upload(uploadData);
		String post = upload.post();// json to server
		if (post != null && post.equals("worked"))
		{
			Date date = new Date();
			theUser.setDate(date);
		}
		if (ServerUtilities.checkForSurvey(this))
		{
			InterfaceUtilities.createNotification(this);
		}

		// Check to see if we have internet before asking for Facebook data
		try
		{
			InetAddress.getByName("api.facebook.com");
		}
		catch (UnknownHostException e)
		{
			// No internet
			Log.i("ERROR", "Could not access Facebook, must not have internet");
			return;
		}
		
		
		if (theUser.getTokenAgeLong() != UploadRegistration.SKIPPED_FACEBOOK)
		{
			System.out.println("Starting facebook reauth");
			Intent newFaceBookToken = new Intent(this, ReAuthenticate.class);
			newFaceBookToken.putExtra("phone_number", theUser.getUser());
			newFaceBookToken.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(newFaceBookToken);
		}
		else
		{
			System.out.println("Not starting Facebook reauth");
		}
	}

}

package post_registration;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;

import sms_messages.Upload;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PostTokenIntent extends IntentService
{

	public PostTokenIntent()
	{
		super("PostTokenIntent");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		String number = null;
		String token = null;
		if (extras != null)
		{
			number = extras.getString("phone_number");
			token = extras.getString("token");
		}
		JSONObject uploadData = new JSONObject();
		try
		{
			uploadData.put("user", number);
			uploadData.put("facebook_token", token);
		}
		catch (JSONException e1)
		{
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		Upload newToken = new Upload(uploadData);
		newToken.postToken();
		SharedPreferences sharedPref = getApplicationContext()
				.getSharedPreferences("mypref", 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("faceToken",token);
		editor.commit();
		
		this.stopService(intent);
		
	}

}

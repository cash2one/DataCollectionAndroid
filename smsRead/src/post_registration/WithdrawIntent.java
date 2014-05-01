package post_registration;

import org.json.JSONException;
import org.json.JSONObject;

import sms_messages.Upload;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class WithdrawIntent extends IntentService
{

	public WithdrawIntent()
	{
		super("Withdraw Intent");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		String number = null;
		
		if (extras != null)
		{
			number = extras.getString("phone_number");
		}
		
		JSONObject uploadData = new JSONObject();
		
		try
		{
			uploadData.put("user", number);
		}
		catch (JSONException e1)
		{
			
		}
		
		Upload newToken = new Upload(uploadData);
		newToken.postWithdrawRequest();
	}

}

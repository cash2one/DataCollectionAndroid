package utilities;

import java.io.IOException;
import java.net.ConnectException;

import main.MainActivity;

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

import sms_messages.User;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ServerUtilities
{
	public static boolean checkForSurvey(Context context)
	{
		HttpPost post = new HttpPost(MainActivity.SURVEY_URL);
		JSONObject userID = new JSONObject();
		HttpResponse resp = null;
		HttpClient httpclient = new DefaultHttpClient();
		String result = null;
		try
		{
			userID.put("user", new User(context).getUser());
			post.setEntity(new ByteArrayEntity(userID.toString().getBytes()));
			resp = httpclient.execute(post);
			result = EntityUtils.toString(resp.getEntity());
		}
		catch (JSONException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			if (e instanceof ConnectException)
			{
				Log.i("ERROR", "Unable to connect to server");
				return false;
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("result", result);
		if (!(result.equals("null")))
		{
			Uri uri = Uri.parse(result);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		}
		return false;
	}
}

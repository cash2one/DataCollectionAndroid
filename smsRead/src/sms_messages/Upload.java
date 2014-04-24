package sms_messages;

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
import org.json.JSONObject;

import android.util.Log;

/**
 * Author Max Uploads json to server
 * 
 */
public class Upload
{
	private byte[] data;

	public Upload(JSONObject json)
	{
		this.data = json.toString().getBytes();
	}

	/**
	 * 
	 * By Tom Empty constructor, can't be used for posting data. Its used for
	 * the post registration activity
	 * 
	 */
	public Upload()
	{

	}

	public String post()
	{
		HttpPost post = new HttpPost(MainActivity.ANDROID_UPLOAD_URL);
		post.setEntity(new ByteArrayEntity(data));
		HttpResponse resp = null;
		HttpClient httpclient = new DefaultHttpClient();
		try
		{
			resp = httpclient.execute(post);
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		String result = null;
		try
		{
			if (resp != null)
				result = EntityUtils.toString(resp.getEntity());
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String postToken()
	{
		HttpPost post = new HttpPost(MainActivity.POST_TOKEN_URL);
		post.setEntity(new ByteArrayEntity(data));
		HttpResponse resp = null;
		HttpClient httpclient = new DefaultHttpClient();
		try
		{
			resp = httpclient.execute(post);
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			if (e instanceof ConnectException)
			{
				Log.i("ERROR", "Unable to connect to server");
				return null;
			}
			e.printStackTrace();
		}
		String result = null;
		try
		{
			result = EntityUtils.toString(resp.getEntity());

		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}

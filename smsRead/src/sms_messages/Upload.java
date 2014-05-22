package sms_messages;

import java.io.IOException;

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
			System.out.println("ERROR: Unable to connect to server");
			System.out.println(e.getMessage());
			return null;
		}
		
		String result = null;
		try
		{
			if (resp != null)
				result = EntityUtils.toString(resp.getEntity());
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
			return null;
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
			System.out.println("ERROR: Unable to connect to server");
			System.out.println(e.getMessage());
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

	public String postWithdrawRequest()
	{
		HttpPost post = new HttpPost(MainActivity.WITHDRAW_REQUEST_URL);
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
			System.out.println("ERROR: Unable to connect to server");
			System.out.println(e.getMessage());
			return null;
		}
		String result = null;
		try
		{
			result = EntityUtils.toString(resp.getEntity());

		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("ERROR: Unable to connect to server");
			System.out.println(e.getMessage());
			return null;
		}
		return result;
	}
}

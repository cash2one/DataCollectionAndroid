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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 15000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		String result = null;
		try{
			resp = httpclient.execute(post);
			if (resp != null)
				result = EntityUtils.toString(resp.getEntity());
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
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public String postToken()
	{
		HttpPost post = new HttpPost(MainActivity.POST_TOKEN_URL);
		post.setEntity(new ByteArrayEntity(data));
		HttpResponse resp = null;
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 15000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		String result = null;
		try{
			resp = httpclient.execute(post);
			if (resp != null)
				result = EntityUtils.toString(resp.getEntity());
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
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	

	public String postWithdrawRequest()
	{
		HttpPost post = new HttpPost(MainActivity.WITHDRAW_REQUEST_URL);
		post.setEntity(new ByteArrayEntity(data));
		HttpResponse resp = null;
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 15000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		String result = null;
		try{
			resp = httpclient.execute(post);
			if (resp != null)
				result = EntityUtils.toString(resp.getEntity());
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
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return result;
	}
}

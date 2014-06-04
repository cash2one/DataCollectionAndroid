package sms_messages;

import java.io.IOException;

import main.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
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

	public String post()
	{
		HttpPost post = new HttpPost(MainActivity.ANDROID_UPLOAD_URL);
		post.setEntity(new ByteArrayEntity(data));
		HttpResponse resp = null;
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// Time out is set as a worst case and is based on test with 800 messages sent over 3G
		int timeoutConnection = 240000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 240000;
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
			e.printStackTrace();
			return "null";
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
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 30000;
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
			return "null";
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
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 30000;
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
			return "null";
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public String checkToken(String token)
	{
		String baseUrl = "https://graph.facebook.com/me?access_token=";
		baseUrl = baseUrl + token;
		HttpGet get = new HttpGet(baseUrl);
		HttpResponse resp;
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// Time out is set as a worst case and is based on test with 800 messages sent over 3G
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 30000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		String result = "null";
		try{
			resp = httpclient.execute(get);
			if (resp != null){
				result = EntityUtils.toString(resp.getEntity());
				Log.i("result",result);
				JSONObject json = new JSONObject(result);
				result = String.valueOf(json.has("error"));
				}
			}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("ERROR: Unable to connect to server");
			e.printStackTrace();
			return "null";
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch(JSONException e){
			e.printStackTrace();
			return "null";
		}
		return result;
	}
}

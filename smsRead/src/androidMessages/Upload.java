package androidMessages;

import java.io.IOException;

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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Uploads json to server
 *
 */
public class Upload {
	private byte[] data;

	public Upload(JSONObject json) {
		this.data = json.toString().getBytes();
	}

	public String post() {
		HttpPost post = new HttpPost(
				"http://172.23.6.179:8001/DataCollection/postandroid/");
		post.setEntity(new ByteArrayEntity(data));
		HttpResponse resp = null;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			resp = httpclient.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = null;
		try {
			result = EntityUtils.toString(resp.getEntity());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public void checkForServey(Context context, User user) {
		HttpPost post = new HttpPost(
				"http://172.23.6.179:8001/DataCollection/servey/");
		JSONObject userID = new JSONObject();
		try {
			userID.put("user", user.getUser());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		post.setEntity(new ByteArrayEntity(userID.toString().getBytes()));
		HttpResponse resp = null;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			resp = httpclient.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = null;
		try {
			result = EntityUtils.toString(resp.getEntity());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("result",result);
		if(!(result.equals("null"))){
			Uri uri = Uri.parse(result);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}
}
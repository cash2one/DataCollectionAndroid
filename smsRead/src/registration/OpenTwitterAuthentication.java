package registration;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * Internal class used for opening the browser for twitter authentication
 * 
 */
public class OpenTwitterAuthentication extends
		AsyncTask<String, String, RequestToken>
{
	private Activity context;

	public OpenTwitterAuthentication(Activity context)
	{
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(RequestToken requestToken)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(requestToken.getAuthenticationURL()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
		context.startActivity(intent);
		context.finish();
	}

	@Override
	protected RequestToken doInBackground(String... params)
	{
		return TwitterUtilities.getInstance().getRequestToken();
	}
}

package post_registration;

import utilities.ServerUtilities;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class CheckForSurveys extends AsyncTask<Void, Void, Boolean>
{
	private Context context;

	public CheckForSurveys(Context context)
	{
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		return ServerUtilities.checkForSurvey(context);
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		if (result == false)
		{
			Toast.makeText(context,
					"Thanks for checking but you've finished all the surveys!",
					Toast.LENGTH_LONG).show();
		}
	}
}

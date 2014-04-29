package utilities;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.util.Log;

public class InternetUtilities
{
	public static boolean hasFacebookAccess()
	{
		AsyncTask<Void, Boolean, Boolean> task = new AsyncTask<Void, Boolean, Boolean>()
		{
			protected Boolean doInBackground(Void... params)
			{
				// Check to see if we have internet before asking for Facebook
				// data
				try
				{
					InetAddress.getByName("api.facebook.com");
				}
				catch (UnknownHostException e)
				{
					// No internet
					Log.i("ERROR",
							"Could not access Facebook, must not have internet");
					return false;
				}
				return true;
			}

		};
		try
		{
			return task.execute().get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

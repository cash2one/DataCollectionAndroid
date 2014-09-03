package sms_messages;

import java.text.SimpleDateFormat;
import java.util.Date;

import main.MainActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Access to phone number set in the user creation activity.
 */
public class User
{
	private String user;
	private Date date;
	private Date tokenAge;
	private Context context;
	private long tokenAgeLong;
	private Date lastFail;
	private boolean hasFace;
	private String faceToken;

	public User(Context context)
	{
		this.context = context;
		SharedPreferences sharedPref = context.getApplicationContext()
				.getSharedPreferences("mypref", 0);
		
		this.user = sharedPref.getString("phone_number", "");
		this.hasFace = sharedPref.getBoolean("hasFace", false);
		this.faceToken = sharedPref.getString("faceToken", "");
		Long DStartDate = (long) 0;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			Date Startdate = sdf.parse(MainActivity.STARTINGDATE);
			DStartDate = Startdate.getTime();
		}
		catch (Exception e) {
			//Log.e("Date error in user", "error",e);
		}
		long temp = sharedPref.getLong("lastUploaded", DStartDate);
		this.date = new Date(temp);
		
		temp = sharedPref.getLong("tokenAge", 0);
		this.tokenAge = new Date(temp);
		this.tokenAgeLong = temp;
		

		temp = sharedPref.getLong("lastFail", 0);
		lastFail = new Date(temp);
	}

	public Date getTokenAge()
	{
		return tokenAge;
	}

	public long getTokenAgeLong()
	{
		return tokenAgeLong;
	}

	public void setTokenAge(Date tokenAge)
	{
		this.tokenAge = tokenAge;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		// sets date in user and in shared preferences
		this.date = date;
		SharedPreferences sharedPref = context.getApplicationContext()
				.getSharedPreferences("mypref", 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong("lastUploaded", date.getTime());
		// commits to save
		editor.commit();
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getUser()
	{
		return this.user;
	}

	public void setLastFailed(Date date)
	{
		// sets date in user and in shared preferences
		this.lastFail = date;
		SharedPreferences sharedPref = context.getApplicationContext()
				.getSharedPreferences("mypref", 0);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong("lastFail", date.getTime());
		// commits to save
		editor.commit();
	}
	
	public Date getLastFail()
	{
		return lastFail;
	}
	
	public boolean getHasFace(){
		return hasFace;
	}
	public String getToken(){
		return faceToken;
	}
}

package androidMessages;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Access to phone number set in the user creation activity.
 */
public class User {
	private String user;
	private Date date;
	private Context context;
	public User(Context context) {
		this.context = context;
		String user = null;
		SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("mypref", 0);
		this.user = sharedPref.getString("phoneNumber", "");
		long temp = sharedPref.getLong("date", 0);
		this.date = new Date(temp);
		Log.i("test", this.user);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		//sets date in user and in shared preferences
		this.date = date;
	     SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("mypref", 0);
	     SharedPreferences.Editor editor= sharedPref.edit();
	     editor.putLong("date", date.getTime());
	   //commits to save
	     editor.commit();
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return this.user;
	}

}

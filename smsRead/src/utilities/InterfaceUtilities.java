package utilities;

import post_registration.SecondaryActivity;
import post_registration.WithdrawIntent;
import sms_messages.User;
import alarmreceiver.AlarmReceiver;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;
import edu.uiowa.datacollection.sms.R;

public class InterfaceUtilities
{
	public static final String helpMessage = "Registration steps:"
			+ "\n\t1) Press the Facebook button and login."
			+ "\n\t2) Press the Twitter button and login."
			+ "\n\t3) Enter your phone number."
			+ "\n\t4) Press submit."
			+ "\nQuestions or comments? Contact support@uiowa.cyberbullying.edu";
	public static final String helpTitle = "Registration help";

	public static final String privacyStatement = "All data will be stored on a secure "
			+ "University of Iowa server. No unauthorized users will have access "
			+ "to the data and it will be anonymized. No details of your or "
			+ "your friends will ever be shared.";
	public static final String privacyTitle = "Privacy Statement";

	public static final String errorStatement = "Found a bug? Have suggestions for us? "
			+ "Want more information about this study?\n"
			+ "Contact us at support@uiowa.cyberbullying.edu";
	public static final String errorTitle = "Report an Error";

	public static final String withdrawStatement = "Want to withdraw from the study? Email "
			+ "us at support@uiowa.cyberbullying.edu";
	public static final String withdrawTitle = "Withdraw from Study";

	public static void createHelpDialog(final Activity context)
	{

		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Help");

		Drawable myIcon = context.getResources().getDrawable(
				R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(context.getResources(),
				bitmapResized);

		builder.setIcon(icon);

		String[] options = new String[] { "Registration Help",
				"Privacy Statement", "Report an Error", "Withdraw from study" };
		builder.setItems(options, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0)
				{
					createInfoDialogWithExitButtonAndCustomAction(helpMessage,
							helpTitle, false, "Email us",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									String recepientEmail = "support@uiowa.cyberbullying.edu";
									Intent intent = new Intent(
											Intent.ACTION_SENDTO);
									intent.setData(Uri.parse("mailto:"
											+ recepientEmail));
									context.startActivity(intent);
								}
							}, context);
				}
				else if (which == 1)
				{
					createInfoDialogWithExitButton(privacyStatement,
							privacyTitle, false, context);
				}
				else if (which == 2)
				{
					createInfoDialogWithExitButtonAndCustomAction(
							errorStatement, errorTitle, false, "Email us",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									String recepientEmail = "support@uiowa.cyberbullying.edu";
									Intent intent = new Intent(
											Intent.ACTION_SENDTO);
									intent.setData(Uri.parse("mailto:"
											+ recepientEmail));
									context.startActivity(intent);
								}
							}, context);
				}
				else if (which == 3)
				{
					createInfoDialogWithExitButtonAndCustomAction(
							withdrawStatement, withdrawTitle, false,
							"Withdraw", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									withdraw(context);
								}
							}, context);
				}
			}
		});

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	private static void withdraw(final Activity context)
	{
		Intent intent = new Intent(context, WithdrawIntent.class);
		intent.putExtra("phone_number", new User(context).getUser());

		// Disable the alarm
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		
		Intent alarm = new Intent(context, AlarmReceiver.class);
		
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 7,
				alarm, PendingIntent.FLAG_CANCEL_CURRENT);
		
		alarmMgr.cancel(alarmIntent);

		Toast.makeText(context, "Withdrawing...", Toast.LENGTH_SHORT).show();

		// Send something to the server
		context.startService(intent);
	}

	public static void createInfoDialogWithExitButtonAndCustomAction(
			String text, String title, final boolean closeOnExit,
			String actionButtonText,
			DialogInterface.OnClickListener onClickListener,
			final Activity context)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		Drawable myIcon = context.getResources().getDrawable(
				R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(context.getResources(),
				bitmapResized);

		builder.setIcon(icon);

		builder.setMessage(text);
		builder.setPositiveButton(actionButtonText, onClickListener);
		builder.setNegativeButton("Exit", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (closeOnExit)
					context.finish();
			}
		});
		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	/**
	 * @param closeOnExit
	 */
	public static void createInfoDialogWithExitButton(String text,
			String title, final boolean closeOnExit, final Activity context)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		Drawable myIcon = context.getResources().getDrawable(
				R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(context.getResources(),
				bitmapResized);

		builder.setIcon(icon);

		builder.setMessage(text);
		builder.setNegativeButton("Exit", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				if (closeOnExit)
					context.finish();
			}
		});
		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	/**
	 * Creates the Skip or Login dialog for Facebook or twitter
	 */
	public static void createLoginDialog(Activity context, String loginText,
			DialogInterface.OnClickListener loginAction, String skipText,
			DialogInterface.OnClickListener skipAction, String message,
			String title)
	{
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		Drawable myIcon = context.getResources().getDrawable(
				R.drawable.ic_launcher);

		BitmapDrawable bd = (BitmapDrawable) myIcon;
		Bitmap bitmapResized = Bitmap.createScaledBitmap(bd.getBitmap(), 50,
				50, false);
		Drawable icon = new BitmapDrawable(context.getResources(),
				bitmapResized);

		builder.setIcon(icon);

		builder.setMessage(message);
		builder.setPositiveButton(loginText, loginAction);
		builder.setNegativeButton(skipText, skipAction);

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
	}

	public static void createNotification(Context context)
	{
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.notif_icon)
				.setContentTitle("Survey")
				.setContentText("Please complete the survey")
				.setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, SecondaryActivity.class);
		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SecondaryActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		
		mNotificationManager.notify(0, mBuilder.build());
	}
}

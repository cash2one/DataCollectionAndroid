package utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import edu.uiowa.datacollection.sms.R;

public class DialogUtilities
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
							"Email us", new DialogInterface.OnClickListener()
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
			}
		});

		dialog = builder.create();// AlertDialog dialog; create like this
									// outside onClick
		dialog.show();
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
			DialogInterface.OnClickListener skipAction, String message, String title)
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

}

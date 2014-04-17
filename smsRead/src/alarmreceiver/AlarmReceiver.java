package alarmreceiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import androidMessages.MessageService;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast
 * Intent and then starts the IntentService MessageService.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
	// The app's AlarmManager, which provides access to the system alarm
	// services.
	private AlarmManager alarmMgr;
	// The pending intent that is triggered when the alarm fires.
	private PendingIntent alarmIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		 * Starts the messageSercive when alarm is triggered to collect sms/mms.
		 */
		Intent service = new Intent(context, MessageService.class);

		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, service);
	}

	public void setAlarm(Context context) {
		alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 7, intent, 0);

		/*
		 * Wake up the device to fire the alarm in a day, and every day after
		 * that. It uses inexactRepeating and elapsed_realtime to add some
		 * randomness so that not all users are trying to upload at the same
		 * time.
		 */
//		alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//				AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY,
//				alarmIntent);

		/*
		 * test alarm goes off right away and every minute after.
		 */
		 Calendar calendar = Calendar.getInstance();
		 alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
		 calendar.getTimeInMillis(),
		 1000 * 60, alarmIntent);

		// Enable BootReceiver to automatically restart the alarm when
		// the device is rebooted.
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}
}

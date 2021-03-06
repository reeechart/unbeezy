package com.sams.unbeezy;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sams.unbeezy.receivers.AlarmReceiver;
import com.sams.unbeezy.models.AlarmModel;

import java.util.Calendar;

/**
 * Created by wennyyustalim on 19/02/18.
 */

public class AddAlarmActivity extends BaseActivity {
    String ACTIVITY_TITLE = "Add New Alarm";
    String LOG_TAG = "AddAlarmActivity";
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private TextView alarmTextView;

    AlarmModel newAlarm = new AlarmModel();

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        setToolbar(ACTIVITY_TITLE);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarm_time_picker);
        alarmTextView = (TextView) findViewById(R.id.alarm_text);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    public void onToggleClicked(View view) {
        String toastMessage;

        if(((ToggleButton) view).isChecked()) {
            newAlarm.switchOn();
            toastMessage = getString(R.string.alarm_on_toast);
        } else {
            newAlarm.switchOff();
            toastMessage = getString(R.string.alarm_off_toast);
        }
        Toast.makeText(AddAlarmActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public void onSaveButtonClicked(View view) {
        newAlarm.setHour(alarmTimePicker.getCurrentHour());
        newAlarm.setMinute(alarmTimePicker.getCurrentMinute());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newAlarm", gson.toJson(newAlarm));
        setResult(RESULT_OK, resultIntent);

        finish();
    }
}

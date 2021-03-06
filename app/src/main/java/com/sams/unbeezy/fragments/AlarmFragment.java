package com.sams.unbeezy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.sams.unbeezy.AddAlarmActivity;
import com.sams.unbeezy.R;
import com.sams.unbeezy.controllers.AlarmFragmentController;
import com.sams.unbeezy.models.AlarmModel;

import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kennethhalim on 2/12/18.
 */

public class AlarmFragment extends Fragment {
    public static final int NEW_ALARM_REQUEST = 1;
    private String LOG_TAG = "AlarmFragment";

    LinearLayout alarmListView;

    AlarmFragmentController controller;
    private static AlarmFragment _instance;

    Gson gson = new Gson();

    public AlarmFragment() {
        controller = new AlarmFragmentController(this);
    }

    public static AlarmFragment getInstance() {
        if(_instance == null) {
            _instance = new AlarmFragment();
        }
        return _instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_alarm, container, false);

        // Plus button to add alarm
        FloatingActionButton alarmFAB = rootView.findViewById(R.id.alarm_insert_fab);
        alarmFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddAlarmActivity.class);
                startActivityForResult(intent, NEW_ALARM_REQUEST);
            }
        });
        alarmListView = rootView.findViewById(R.id.alarm_list);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller = new AlarmFragmentController(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ALARM_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Add new alarm via controller
                String intentString = data.getStringExtra("newAlarm");
                AlarmModel newAlarm = gson.fromJson(intentString,AlarmModel.class);
                controller.addData(newAlarm);
            }
        }
    }

    public void updateLayout(final Map<String,AlarmModel> alarmsArray) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptLinearLayout(alarmListView, alarmsArray);
            }
        });
    }

    private void adaptLinearLayout(LinearLayout layout, Map<String,AlarmModel> alarmsArray) {
        layout.removeAllViews();
        Log.d(LOG_TAG, gson.toJson(alarmsArray));
        int height = 0;
        for (Map.Entry<String, AlarmModel> item : alarmsArray.entrySet()) {
            View inflated = inflateLayout(item.getKey(),item.getValue(), layout);
            layout.addView(inflated);
            height += inflated.getMeasuredHeight();
        }
        layout.getLayoutParams().height = height;
    }

    private View inflateLayout(final String key, AlarmModel model, ViewGroup parent) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflated = inflater.inflate(R.layout.component_alarms_list_view, parent, false);
        inflated.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                controller.deleteData(key);
                return true;
            }
        });
        ToggleButton toggleButton = inflated.findViewById(R.id.alarm_toggle_button);
        toggleButton.setChecked(model.isOn());
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                controller.toggleData(key,isChecked);
            }
        });
        TextView alarmTime = inflated.findViewById(R.id.alarm_time);
        String hour = Integer.toString(model.getHour());
        String minute = Integer.toString(model.getMinute());
        if (model.getHour() < 10) {
            hour = "0" + hour;
        }
        if (model.getMinute() < 10) {
            minute = "0" + minute;
        }
        alarmTime.setText(String.format("%s:%s", hour, minute));

        return inflated;
    }
}

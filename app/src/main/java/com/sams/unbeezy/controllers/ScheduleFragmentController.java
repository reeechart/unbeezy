package com.sams.unbeezy.controllers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sams.unbeezy.fragments.ScheduleFragment;
import com.sams.unbeezy.models.CourseModel;
import com.sams.unbeezy.models.SchedulesItemModel;
import com.sams.unbeezy.models.SchedulesModel;
import com.sams.unbeezy.services.FirebaseDatabaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kennethhalim on 2/21/18.
 */

public class ScheduleFragmentController {
    List<CourseModel> coursesData;
    DatabaseReference coursesDataRef;
    ScheduleFragment fragment;
    DatabaseReference scheduleDataRef;
    SchedulesModel schedulesData;

    Gson gson;
    static String LOG_TAG = "UNBEEZY_COURSES_CONTROLLER";
    public ScheduleFragmentController(ScheduleFragment fragment) {
        this.fragment = fragment;
        coursesDataRef = FirebaseDatabaseService.getInstance().child("courses");
        scheduleDataRef = FirebaseDatabaseService.getInstance().child("schedules");
        gson = new Gson();
        updateData();
        updateScheduleData();
    }
    public void updateData() {
        coursesDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coursesData = new ArrayList<>();
                if(dataSnapshot.getValue() != null) {
                   for(DataSnapshot item : dataSnapshot.getChildren()) {
                       coursesData.add(item.getValue(CourseModel.class));
                   }
                   fragment.updateLayout(coursesData);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void addData(CourseModel model) {
        coursesDataRef.push().setValue(model);
        updateData();
    }

    public void updateScheduleData() {
        scheduleDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                schedulesData = new SchedulesModel();
                for(DataSnapshot i : dataSnapshot.getChildren()) {
                    for(DataSnapshot j : i.getChildren()) {
                        schedulesData.getData()[Integer.parseInt(i.getKey())][Integer.parseInt(j.getKey())] = j.getValue(SchedulesItemModel.class);
                    }

                }
                fragment.updateScheduleTable(schedulesData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

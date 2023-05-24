package com.example.smalltemi;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

public class MainActivity extends AppCompatActivity implements
        OnRobotReadyListener {



    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    private Robot robot;
    public MainActivity() {this.robot =  Robot.getInstance();}
    public Robot getRobot() {return robot;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void skidJoy(View view) {
        long t = System.currentTimeMillis();
        long end = t + 4000;
        while (System.currentTimeMillis() < end) {
            robot.skidJoy(1F, 0F);
            if(System.currentTimeMillis() == end - 2000){
                break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        robot.addOnRobotReadyListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        robot.removeOnRobotReadyListener(this);
    }
    @Override
    public void onRobotReady(boolean isReady) {
        if (isReady) {
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                // Robot.getInstance().onStart() method may change the visibility of top bar.
                robot.onStart(activityInfo);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
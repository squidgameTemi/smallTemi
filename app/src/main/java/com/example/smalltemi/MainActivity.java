package com.example.smalltemi;

import static android.content.ContentValues.TAG;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

public class MainActivity extends AppCompatActivity implements
        OnRobotReadyListener {



    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    private Robot robot;
    public MainActivity() {this.robot =  Robot.getInstance();}
    public Robot getRobot() {return robot;}

    int g_start, one_life, two_life;

    public void goTo(String destination) {
        for (String location : robot.getLocations()) {
            if (location.equals(destination.toLowerCase().trim())) {
                robot.goTo(destination.toLowerCase().trim());
            }
        }
    }

    public void skidJoy1() {
        long t = System.currentTimeMillis();
        long end = t + 4000;
        while (System.currentTimeMillis() < end) {
            robot.skidJoy(1F, 0F);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference.child("member").child("one").setValue(1);
        databaseReference.child("member").child("two").setValue(1);
        databaseReference.child("win").child("one").setValue(0);
        databaseReference.child("win").child("two").setValue(0);

        databaseReference.child("game").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Object rdata = snapshot.getValue();
                g_start=Integer.parseInt(rdata.toString());
                if(g_start == 0){   //게임종료 눌렀을 때 제자리로 돌아가기
                    goTo("일");
                }
                if(g_start > 0){
                    databaseReference.child("member/one").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            Object rdata = snapshot.getValue();
                            one_life=Integer.parseInt(rdata.toString());
                            if(one_life == 0){
                                long t = System.currentTimeMillis();
                                long end = t + 4000;
                                while (System.currentTimeMillis() < end) {
                                    robot.skidJoy(1F, 0F);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void win(View view){
        databaseReference.child("win").child("one").setValue(1);
        Toast.makeText(getApplication(),"1번 통과하셨습니다", Toast.LENGTH_SHORT).show();
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
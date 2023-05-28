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

    int g_start, g_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference.child("member").child("one").setValue(1);
        databaseReference.child("member").child("two").setValue(1);
        databaseReference.child("win").child("one").setValue(0);
        databaseReference.child("win").child("two").setValue(0);
        databaseReference.child("sensor").child("on").setValue(0);

        databaseReference.child("game/start").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Object rdata = snapshot.getValue();
                g_start=Integer.parseInt(rdata.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        if(g_start == 1){
            //센서 작동
            databaseReference.child("sensor").child("on").setValue(1);
        }

        /*광학측정기로 플레이어의 움직임을 감지
        if(){ 움직임 감지한다면
            databaseReference.child("member").child("one").setValue(0); 1플레이어 탈락
            조교 테미는 앞으로 이동하여 물총 발사
            long t = System.currentTimeMillis();
            long end = t + 4000;    //4초 동안 앞으로 전진
            while (System.currentTimeMillis() < end) {
                robot.skidJoy(1F, 0F);
            }
        }*/

    }

    public void win(View view){
        databaseReference.child("win").child("one").setValue(1);
        Toast.makeText(getApplication(),"1번 통과하셨습니다", Toast.LENGTH_SHORT).show();
    }



    public void skidJoy(View view) {
        long t = System.currentTimeMillis();
        long end = t + 4000;
        while (System.currentTimeMillis() < end) {
            robot.skidJoy(1F, 0F);
            /*if(System.currentTimeMillis() == end - 2000){
                break;
            }*/
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
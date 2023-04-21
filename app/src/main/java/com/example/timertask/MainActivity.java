package com.example.timertask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.timertask.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private CountDownTimer workoutTimer,restTimer;
    private long workoutTimeLeftInMillis, restTimeLeftInMillis;
    private int userWorkoutTime, restWorkoutTime;
    private String timeLeftString;
    private boolean restWorkoutFlag = false;
    private int min,sec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(String.valueOf(binding.workoutEditText.getText()).trim())) {
                    Toast.makeText(MainActivity.this, "Enter Workout Time..", Toast.LENGTH_SHORT).show();
                    return;
                }
                startTimer();
            }
        });

        binding.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(String.valueOf(binding.workoutEditText.getText()).trim())) {
                    Toast.makeText(MainActivity.this, "Enter Workout Time..", Toast.LENGTH_SHORT).show();
                    return;
                }
                stopTimer();
            }
        });
    }

    public void startTimer(){
        userWorkoutTime = Integer.valueOf(String.valueOf(binding.workoutEditText.getText()));
        if(!TextUtils.isEmpty(String.valueOf(binding.restDurationEdittext.getText()).trim())) {
            restWorkoutFlag = true;
            restWorkoutTime = Integer.valueOf(String.valueOf(binding.restDurationEdittext.getText()));
        }
        Log.v("REST",String.valueOf(restWorkoutFlag));
        workoutTimeLeftInMillis = TimeUnit.SECONDS.toMillis(userWorkoutTime);
        workoutTimer = new CountDownTimer(workoutTimeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                workoutTimeLeftInMillis = l;
                updateUI(l,false);
            }

            @Override
            public void onFinish() {
                sound();
                if(restWorkoutFlag) {
                    if (this == restTimer) {
                        // Rest timer has finished
                        startTimer();
                        Toast.makeText(MainActivity.this, "Rest Time is Completed..", Toast.LENGTH_SHORT).show();
                        notifyUser("Rest timer has finished!");
                    } else {
                        // Workout timer has finished
                        restTimerStart();
                        Toast.makeText(MainActivity.this, "Workout Time is Completed..", Toast.LENGTH_SHORT).show();
                        notifyUser("Workout timer has finished!");
                    }
                } else {
                    // Only workout timer is running
                    Toast.makeText(MainActivity.this, "Workout Time is Completed..", Toast.LENGTH_SHORT).show();
                    notifyUser("Workout timer has finished!");
                }
            }

        }.start();
    }

    public void restTimerStart() {
        restTimeLeftInMillis = TimeUnit.SECONDS.toMillis(restWorkoutTime);
        restTimer = new CountDownTimer(restTimeLeftInMillis,1000) {
            @Override
            public void onTick(long R) {
                restTimeLeftInMillis = R;
                updateUI(R,true);
            }

            @Override
            public void onFinish() {
                sound();
                startTimer();
                Toast.makeText(MainActivity.this, "Rest Time is Completed..", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    public void stopTimer () {
        workoutTimer.cancel();
        if(restWorkoutFlag) {
            restTimer.cancel();
        }
    }

    public void updateUI(long time, boolean restFlag){
        int min = (int) time / 60000;
        int sec = (int) time % 60000 / 1000;
        binding.progressBar.setProgress(sec);
        timeLeftString = "" + min + ":";
        if (sec<10) timeLeftString += "0";
        timeLeftString += sec;
        if(restFlag) {
            binding.restTimerTextview.setText("Rest Time Remaining: "+timeLeftString);
        } else {
            binding.workoutTimerTextview.setText("Workout Time Remaining: "+timeLeftString);
        }
    }

    public void notifyUser(String message) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.timer)
                .setContentTitle("Workout Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager.notify(0, builder.build());
    }




    public void sound() {
        notifyUser("Rest time is finished");
        // Create a MediaPlayer instance and load the sound file
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alert);

        // Set a completion listener to release the MediaPlayer resources when the sound finishes playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

        // Start the sound playback
        mediaPlayer.start();
    }
}

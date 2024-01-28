package com.sp.learntogether.ui.planner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentPlannerBinding;


public class PlannerFragment extends Fragment {

    private FragmentPlannerBinding binding;
    MediaPlayer mp;
    private EditText hourText, minuteText, secondsText;
    private String hourString, minutesString, secondsString;
    private Integer hours, minutes, seconds;
    private long startTimeInMillis, timeLeftInMillis, timeElapsedInMillis, endTime;
    private float rHours, savedTime;
    private Button startButton, pauseButton, resetButton;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private String selectedTask;

    private static final String CHANNEL_ID = "timer_channel";
    private NotificationManager notificationManager;

    ViewGroup allViews;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlannerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        allViews = (ViewGroup) view;

        create_notification_channel();
        mp = MediaPlayer.create(getContext(),R.raw.googleduoring);

        startButton = binding.startButton;
        pauseButton = binding.pauseButton;
        resetButton = binding.resetButton;

        hourText = binding.hourText;
        minuteText = binding.minuteText;
        secondsText = binding.secondsText;

        //Timer button listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hourString = hourText.getText().toString();
                minutesString = minuteText.getText().toString();
                secondsString = secondsText.getText().toString();

                if(hourString == "" || minutesString == "" || secondsString == "") {
                    Toast.makeText(getContext(), "Error with time input", Toast.LENGTH_SHORT).show();
                    return;
                }

                hours = Integer.parseInt(hourString);
                minutes = Integer.parseInt(minutesString);
                seconds = Integer.parseInt(secondsString);

                if(hours == 0 && minutes == 0 && seconds == 0) {
                    Toast.makeText(getContext(), "Please enter values for timer", Toast.LENGTH_LONG).show();
                    return;
                } else if(minutes > 60 || seconds > 60) {
                    Toast.makeText(getContext(), "Please input maximum '60' for seconds and minutes", Toast.LENGTH_LONG).show();
                    return;
                }

                timeLeftInMillis = calculateStartTime(hours, minutes, seconds);
                startTimeInMillis = timeLeftInMillis;

                startButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);

                hourText.setEnabled(false);
                minuteText.setEnabled(false);
                secondsText.setEnabled(false);

                startTimer();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerRunning) {
                    pauseTimer();

                    pauseButton.setText("Resume");
                } else {
                    startTimer();

                    pauseButton.setText("Pause");
                }

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
        setHasOptionsMenu(true);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.planner_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(timerRunning){
            resetTimer();
        }
        int id = item.getItemId();
        if (id == R.id.addStudyPlace){
            //NavHostFragment.findNavController(this).navigate(R.id.action_planner_fragment_to_addStudyPlace2);
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetTimer() {
        countDownTimer.cancel();
        timerRunning = false;

        timeLeftInMillis = 0;
        hourText.setText("00");
        minuteText.setText("00");
        secondsText.setText("00");

        hourText.setEnabled(true);
        minuteText.setEnabled(true);
        secondsText.setEnabled(true);


        startButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);

    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                timeElapsedInMillis = startTimeInMillis - timeLeftInMillis;

                rHours += (1.00/60.00)/60.00;


                updateCountDownText();
            }

            @Override
            public void onFinish() {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                        .setContentTitle("Study Timer Has Finished")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        ;

                notificationManager.notify(1, builder.build());

                resetTimer();
                mp.start();
            }
        }.start();

        timerRunning = true;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void updateCountDownText() {
        Integer uHours   = (int) ((timeLeftInMillis / (1000*60*60)) % 24);
        Integer uMinutes = (int) ((timeLeftInMillis / (1000*60)) % 60);
        Integer uSeconds = (int) (timeLeftInMillis / 1000) % 60;

        hourText.setText(String.format("%02d", uHours));
        minuteText.setText(String.format("%02d", uMinutes));
        secondsText.setText(String.format("%02d", uSeconds));
    }

    private long calculateStartTime(int hours, int minutes, int seconds) {
        int hoursInMillis, minsInMillis, secsInMillis, totalMillis;

        hoursInMillis = ((hours * 60) * 60) * 1000;
        minsInMillis = (minutes * 60) * 1000;
        secsInMillis = seconds * 1000;

        totalMillis = hoursInMillis + minsInMillis + secsInMillis;
        return totalMillis;
    }

    private void create_notification_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
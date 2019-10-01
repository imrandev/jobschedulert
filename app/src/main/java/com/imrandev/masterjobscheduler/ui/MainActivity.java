package com.imrandev.masterjobscheduler.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.imrandev.masterjobscheduler.R;
import com.imrandev.masterjobscheduler.databinding.ActivityMainBinding;
import com.imrandev.masterjobscheduler.service.MasterJobService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private JobScheduler jobScheduler;
    private ArrayAdapter<String> arrayAdapter;
    private String date;
    private String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());

        mainBinding.lvScheduleLog.setAdapter(arrayAdapter);
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this, MasterJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(5000)
                .build();

        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }
    }

    public void cancelJob(){
        if (jobScheduler != null){
            jobScheduler.cancelAll();
            jobScheduler = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter("job-service"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null){
                String last = date != null ? date : currentTime;
                date = intent.getStringExtra("date-time");
                arrayAdapter.add(String.format("Scheduler Log Time : %s  Interval : %s", date, getTimeDifference(last, date)));
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private String getTimeDifference(String last, String now){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            Date date1 = format.parse(last);
            Date date2 = format.parse(now);
            long mills = date2.getTime() - date1.getTime();
            int hours =(int) mills/(1000 * 60 * 60);
            int mins = (int) (mills/(1000*60)) % 60;
            int secs = (int) (mills / 1000) % 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, secs);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "getTimeDifference: " + e.getMessage());
        }
        return "Not found";
    }
}

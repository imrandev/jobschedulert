package com.imrandev.masterjobscheduler.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.imrandev.masterjobscheduler.task.UpdateTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MasterJobService extends JobService {

    private static final String TAG = "MasterJobService";

    // Called by the Android system when it's time to run the job
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started!");
        String currentDate = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Toast.makeText(getApplicationContext(), currentDate, Toast.LENGTH_LONG).show();

        Intent extras = new Intent("job-service");
        extras.putExtra("date-time", currentDate);
        sendBroadcast(extras);
        jobFinished(jobParameters, true);
        return true;
    }

    private void startWorkOnNewThread(JobParameters jobParameters) {
        new UpdateTask().execute();
    }

    private void doWork(JobParameters jobParameters) {
        Log.d(TAG, "Job finished!");
        jobFinished(jobParameters, true);
    }

    // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before being completed.");
        return false;
    }
}

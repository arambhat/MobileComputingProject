package com.example.covidSymptomTracker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;

class HeartRateMeasurement extends AsyncTask<Uri, Integer, Integer> {

    private final MainActivity mainActivity;

    public HeartRateMeasurement(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected Integer doInBackground(Uri... url) {
        float totalred = 0;
        int peak = 0;
        int totalTimeMilli = 0;
        try {
            File videoPath = mainActivity.getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
            File videoFile = new File(videoPath, MainActivity.FILE_NAME);
            Uri videoFileUri = Uri.parse(videoFile.toString());
            MediaPlayer mp = MediaPlayer.create(mainActivity.getBaseContext(), videoFileUri);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());

            totalTimeMilli = mp.getDuration(); //milli seconds
            int second = 1000000; //
            int imgSize = 100; // Size of the box
            int rate = 4; //number of samples per sec
            int recordingDuration = (int) Math.floor(totalTimeMilli / 1000) * second; //rounding to the nearest second

            int w = 0;
            int h = 0;
            int j = 0;
            float[] diff = new float[imgSize * imgSize];
            float epsilon = 200;
            float prev = 0;
            int no_of_frames = (totalTimeMilli * rate) / 1000;
            for (int i = rate; i <= recordingDuration; i += second / rate) {
                Bitmap bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                //get image size only once
                if (w == 0 || h == 0) {
                    w = bitmap.getWidth();
                    h = bitmap.getHeight();
                }
                totalred = 0;
                // Center box of the image
                for (int x = (w - 2 * imgSize); x < w - imgSize; x++)
                    for (int y = (h - 2 * imgSize); y < h - imgSize; y++) {
                        totalred += Color.red(bitmap.getPixel(x, y));
                    }

                if (j > 0) {
                    diff[j] = Math.abs(totalred - prev);
                    if (diff[j] > epsilon) {
                        peak = peak + 1;
                    }
                } else {
                    // When index is 0
                    diff[j] = 0;
                }
                prev = totalred;
                j += 1;
                onProgressUpdate(j, no_of_frames);
            }
            retriever.release();
        } catch (Exception e) {
            return 0;
        }
        return (peak * 60 * 1000) / totalTimeMilli;
    }

    protected void onProgressUpdate(Integer progress, int frames) {
        mainActivity.setParametersForHR("Processing frames " + Integer.toString(progress) + "/" + frames, false);
    }

    protected void onPostExecute(Integer result) {
        mainActivity.mheartRate_ = Math.round(result);
        mainActivity.setParametersForHR("Heart Rate is " + mainActivity.mheartRate_, true);
    }
}

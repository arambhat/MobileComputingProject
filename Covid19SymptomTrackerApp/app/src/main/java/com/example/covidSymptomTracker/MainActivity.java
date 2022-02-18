package com.example.covidSymptomTracker;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import java.io.File;



public class MainActivity extends AppCompatActivity {
    private Intent mSymptomActivity_;
    private Intent mRespirationService_;
    private DBHandler dbHelper_;
    public int mRespRate_;
    public int mheartRate_;

    HeartRateMeasurement hrt;
    int recordNumber;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mSymptomActivity_ = new Intent(this, SymptomLogger.class);
        dbHelper_ = new DBHandler(MainActivity.this);
        // DB Data length
        recordNumber = dbHelper_.getRecordCount() + 1;

        TextView record = findViewById(R.id.recordCount);
        record.setText("Records " + (recordNumber - 1));
        // Respiratory rate service
        mRespirationService_ = new Intent(getApplicationContext(), SensorHandlerSrv.class);
        ResultReceiver resultReceiver = new RespirationResultReceiver(null);
        mRespirationService_.putExtra(Intent.EXTRA_RESULT_RECEIVER, resultReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();

        displayCount();
    }

    public void navToSymptomActivity(View view) {
        mSymptomActivity_.putExtra("recordCount", recordNumber);

        startActivity(mSymptomActivity_);
    }


   // Heart Rate Related Variables
    private static final int VIDEO_CAPTURE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1996;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    public static final String FILE_NAME = "myvideo.mp4";
    private Uri uriForFile;

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void preInvokeCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.startRecording();
        } else {
            String[] permissionReq = {Manifest.permission.CAMERA};
            requestPermissions(permissionReq, CAMERA_PERMISSION_REQUEST_CODE);

             String[] permissionReq2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionReq2, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Ensure that this result is for the camera permission request
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
             // Check if the request was granted or denied
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                this.startRecording();
            } else {
                // The request was denied -> tell the user and exit the application
                Toast.makeText(this, "Camera permission required.",
                        Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void startRecording() {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mediaFile = new File(videoPath, FILE_NAME);
        uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mediaFile);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        takeVideoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Video saved to: " + Environment.getStorageDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            this.startCalculation();

        } else {
            this.setParametersForHR("Measurement Cancelled", true);
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void measureHeartRate(View view) {
        this.setParametersForHR("Measurement Started", false);
        this.preInvokeCamera();
//        this.startCalculation();
    }

    private void startCalculation() {
        this.setParametersForHR("Video Recorded", false);

        // Set video in the media player
        MediaController m = new MediaController(this);
        VideoView videoView = findViewById(R.id.video_preview);
        videoView.setMediaController(m);
        videoView.setVideoURI(uriForFile);

        hrt = new HeartRateMeasurement(this);
        hrt.execute(uriForFile);

    }


    public void stopHRMeasurement(View view) {
        hrt.cancel(true);
        this.setParametersForHR("Measurement cancelled by user", true);
    }

    void setParametersForHR(String displayText, boolean isDone) {
        Button stopButton = findViewById(R.id.stopHRMeasuring);
        stopButton.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);

        Button hrMeasure = findViewById(R.id.heartRateButton);
        hrMeasure.setVisibility(isDone ? View.VISIBLE : View.INVISIBLE);
        // Stop from the service
        TextView heartRateText = findViewById(R.id.heartRateText);
        heartRateText.setText(displayText);
    }

    public void measureRespiratoryRate(View view) {
        this.setParametersForRR("Measurement Started", false);
        startService(mRespirationService_);
    }

    public void stopRRMeasurement(View view) {
        this.setParametersForRR("Measurement Cancelled", true);
        stopService(mRespirationService_);
    }

    void setParametersForRR(String displayText, boolean isDone) {
        Button stopButton = findViewById(R.id.stopRRMeasuring);
        stopButton.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);

        Button rrMeasure = findViewById(R.id.respiratoryRateButton);
        rrMeasure.setVisibility(isDone ? View.VISIBLE : View.INVISIBLE);

        TextView respirationData = findViewById(R.id.respiratoryText);
        respirationData.setText(displayText);
    }

    public void uploadSigns(View view) {
        DBHandler.SymptomData allSymptoms = dbHelper_.getByID(recordNumber);
        allSymptoms.setRESP_RATE(mRespRate_);
        allSymptoms.setHEART_RATE(mheartRate_);
        if (dbHelper_.updateRecords(allSymptoms, recordNumber)) {
            Toast.makeText(this, "Data saved to DB Record " + recordNumber ,
                    Toast.LENGTH_SHORT).show();
        }
        displayCount();
    }


    @Override
    protected void onDestroy() {
        stopService(mRespirationService_);
        super.onDestroy();
    }

    protected void displayCount() {
        TextView record = findViewById(R.id.recordCount);
        record.setText("Records " + dbHelper_.getRecordCount());
    }


    public class RespirationResultReceiver extends ResultReceiver {

        public RespirationResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == RESULT_OK && resultData != null) {
                mRespRate_ = Integer.parseInt(resultData.getString("Result"));
                setParametersForRR("Recorded peaks " + mRespRate_, false);
            } else if (resultCode == RESULT_CANCELED) {
                stopService(mRespirationService_);
                setParametersForRR("Respiration Rate is " + mRespRate_, true);
            }
        }
    }
}
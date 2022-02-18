package com.example.smarthomegesturetrainer.ui.Practice;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.smarthomegesturetrainer.R;
import com.example.smarthomegesturetrainer.databinding.FragmentPracticeBinding;
import com.example.smarthomegesturetrainer.ui.Learn.LearnViewFragmentArgs;
import com.example.smarthomegesturetrainer.ui.Learn.LearnViewFragmentDirections;

import java.io.File;

@RequiresApi(api = Build.VERSION_CODES.R)
public class PracticeViewFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Practice Fragment";
    private PracticeViewModel practiceViewModel;
    private FragmentPracticeBinding binding;
    private String gesture;
    private String videoName;
    public int practiceNum = 0;
    private Uri uriForFile;
    private String serverUrl;
    final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };
    private FragmentActivity currentContext;

    private ActivityResultLauncher<String[]> requestMultiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(true)) {
                    Log.d(TAG, "Permissions granted");
                    this.startRecording();
                } else {
                    Toast.makeText(getContext(), "The Storage permissions are denied", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            });
    private ActivityResultLauncher<Intent> requestVideointentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
        if (activityResult.getResultCode() == RESULT_OK) {
            Toast.makeText(getContext(), "Practice video " + practiceViewModel.getPracticeVideoName(practiceNum) + " Saved !!", Toast.LENGTH_LONG).show();
            practiceNum = (practiceNum + 1) % 3;
            this.showPreview();
        } else {
            Toast.makeText(getContext(), "Recording Cancelled", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

    });
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        practiceViewModel =
                new ViewModelProvider(this).get(PracticeViewModel.class);
        serverUrl = practiceViewModel.getBaseUrl();
        binding = FragmentPracticeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gesture = PracticeViewFragmentArgs.fromBundle(getArguments()).getGesture();
        videoName = practiceViewModel.getVideoNameFromGesture(gesture);
        final Button button  = binding.practiceButton;
        button.setText("Record");
        button.setOnClickListener(this);

        final Button uploadButton  = binding.uploadButton;
        uploadButton.setText("Upload");
        uploadButton.setOnClickListener(this);

        /*final TextView textView = binding.textPractice;
        practiceViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.practiceButton: {
                this.requestPermissionAndStartRecording();
                break;
            }
            case R.id.uploadButton: {
                this.uploadFiles();
                break;
            }
        }
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void uploadFiles() {
        File videoPath = getActivity().getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File video1 = new File(videoPath, practiceViewModel.getPracticeVideoName(0));
        File video2 = new File(videoPath, practiceViewModel.getPracticeVideoName(1));
        File video3 = new File(videoPath, practiceViewModel.getPracticeVideoName(2));

        MultipartBody.Part videoUpload1 = MultipartBody.Part.createFormData(video1.getName(),
                video1.getName(), RequestBody.create(MediaType.parse("*/*"), video1));
        MultipartBody.Part videoUpload2 = MultipartBody.Part.createFormData(video2.getName(),
                video2.getName(), RequestBody.create(MediaType.parse("*/*"), video2));
        MultipartBody.Part videoUpload3 = MultipartBody.Part.createFormData(video3.getName(),
                video3.getName(), RequestBody.create(MediaType.parse("*/*"), video3));
        RetrofitClientEndpoint getResponse = getRetrofit().create(RetrofitClientEndpoint.class);
        Call<FlaskResponse> apicall = getResponse.uploadMulFile(videoUpload1, videoUpload2, videoUpload3);
        apicall.enqueue(new Callback<FlaskResponse>() {
            @Override
            public void onResponse(Call<FlaskResponse> call, Response<FlaskResponse> response) {
                FlaskResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getContext().getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext().getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Response", serverResponse.toString());
                }
                //FragmentManager manager = getActivity().getSupportFragmentManager();
                //FragmentManager manager = Navigation.findNavController(getActivity(),R.id.);
                //NavHostFragment navHostFragment =  (NavHostFragment) manager.findFragmentById(R.id.nav_host_fragment_activity_main);
                NavController navController = Navigation.findNavController(binding.uploadButton);
                NavDirections action = PracticeViewFragmentDirections.actionNavigationPracticeToNavigationList();
                navController.navigate(action);
            }

            @Override
            public void onFailure(Call<FlaskResponse> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void requestPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                this.startRecording();
            }
        } else {
            requestMultiplePermissionLauncher.launch(PERMISSIONS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void startRecording() {
        File vidPath = getActivity().getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File vidFile = new File(vidPath, practiceViewModel.getPracticeVideoName(practiceNum));
        uriForFile = FileProvider.getUriForFile(getContext().getApplicationContext(), getContext().getApplicationContext().getPackageName() + ".fileprovider", vidFile);
        //Toast.makeText(this,  getFileName(practicenum), Toast.LENGTH_LONG).show();
        try {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            takeVideoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            requestVideointentLauncher.launch(takeVideoIntent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error :" + e.toString(),  Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error: " + e.toString());
        }
    }

    private void showPreview() {
        VideoView videoDisplay = binding.videoView2;
        videoDisplay.setVideoURI(uriForFile);
        videoDisplay.start();
        videoDisplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
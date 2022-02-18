package com.example.smarthomegesturetrainer.ui.Learn;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.smarthomegesturetrainer.databinding.FragmentLearnBinding;
import com.example.smarthomegesturetrainer.ui.List.ListViewFragmentDirections;

import java.io.File;
import java.io.IOError;

@RequiresApi(api = Build.VERSION_CODES.R)
public class LearnViewFragment extends Fragment {

    private static final String TAG = "Learn Fragment";
    private LearnViewModel learnViewModel;
    private FragmentLearnBinding binding;
    private String gesture;
    private String videoName;
    private View currentView;
    private int READ_EXTERNAL_STORAGE_REQUEST_CODE = 100;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    this.showPreview(videoName);
                } else {
                    Toast.makeText(getContext(), "The Storage permissions are denied", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            });


    @RequiresApi(api = Build.VERSION_CODES.R)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        learnViewModel =
                new ViewModelProvider(this).get(LearnViewModel.class);

        binding = FragmentLearnBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gesture = LearnViewFragmentArgs.fromBundle(getArguments()).getGesture();
        videoName = learnViewModel.getVideoNameFromGesture(gesture);
        // Setting up the button listener
        final Button button  = binding.learnButton;
        button.setText("Practice the gesture");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                NavDirections action = LearnViewFragmentDirections.actionNavigationLearnToNavigationPractice3(gesture);
                navController.navigate(action);
            }
        });
        this.requestPermission();
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            try {
                this.showPreview(videoName);
            } catch (IOError ioError) {
                Toast.makeText(getContext(), "Error :" + ioError.toString(),  Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error :" + e.toString(),  Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error: " + e.toString());
            }
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void showPreview(String s) {
        File videoPath = getActivity().getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mediaFile = new File(videoPath, learnViewModel.getVideoName());    // Set video in the media player
        Uri videoFileUri = FileProvider.getUriForFile(getContext().getApplicationContext(), getActivity().getPackageName() + ".fileprovider", mediaFile);
        Log.d("Video URL", "" + videoPath);
        MediaController m = new MediaController(getContext());
        VideoView videoView = binding.videoView;
        videoView.setMediaController(m);
        videoView.setVideoURI(videoFileUri);
        videoView.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
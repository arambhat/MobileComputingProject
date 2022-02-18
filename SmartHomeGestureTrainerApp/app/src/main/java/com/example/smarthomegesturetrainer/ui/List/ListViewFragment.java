package com.example.smarthomegesturetrainer.ui.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.smarthomegesturetrainer.R;
import com.example.smarthomegesturetrainer.databinding.FragmentListBinding;
import com.example.smarthomegesturetrainer.ui.Learn.LearnViewFragment;
import com.example.smarthomegesturetrainer.ui.Learn.LearnViewFragmentDirections;

import java.util.Vector;

public class ListViewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ListViewModel listViewModel;
    private FragmentListBinding binding;
    private FragmentActivity activity;
    private Spinner spinner;
    private int spinnerPosition;
    private String selectedGesture;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.activity = (FragmentActivity) context;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textList;
        // setting text view when this fragment is in active state
        listViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        // Setting up the spinner object
        spinner = binding.spinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.gestures, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        // Setting up the button listener
        final Button button  = binding.listButton;
        button.setText("Learn the gesture");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                /*LearnViewFragment listFragment = new LearnViewFragment();
                Bundle bundle =  new Bundle();
                bundle.putString("gesture", selectedGesture);
                listFragment.setArguments(bundle);*/
                NavDirections action = ListViewFragmentDirections.actionNavigationListToNavigationLearn(selectedGesture);
                navController.navigate(action);
                /*FragmentManager manager = getParentFragmentManager();
                //manager.beginTransaction(R.layout.)*/

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        spinnerPosition = position;
        selectedGesture = (String) adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}


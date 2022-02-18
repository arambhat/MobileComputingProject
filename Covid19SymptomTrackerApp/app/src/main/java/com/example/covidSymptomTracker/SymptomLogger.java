package com.example.covidSymptomTracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomLogger extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RatingBar.OnRatingBarChangeListener {
    public static final String NAUSEA = "Nausea";
    public static final String HEADACHE = "Headache";
    public static final String DIARRHEA = "Diarrhea";
    public static final String SOAR_THROAT = "Soar Throat";
    public static final String FEVER = "Fever";
    public static final String MUSCLE_ACHE = "Muscle Ache";
    public static final String LOSS_OF_SMELL_OR_TASTE = "Loss of Smell or Taste";
    public static final String COUGH = "Cough";
    public static final String SHORTNESS_OF_BREATH = "Shortness of Breath";
    public static final String FEELING_TIRED = "Feeling tired";
    private HashMap<String, Integer> mSymptomsMap_;
    private RatingBar mRatingBar_;
    private Spinner mSpinner_;
    private int mSpinnerPos_;
    private DBHandler mDbHandler_;
    private ListView signList;
    private int mRecordNumber_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);

        mRecordNumber_ = this.getIntent().getIntExtra("recordCount", 0);

        // Initialise hashmap with default values
        mSymptomsMap_ = new HashMap<String, Integer>();

        mRatingBar_ = findViewById(R.id.simpleRatingBar);
        mRatingBar_.setOnRatingBarChangeListener(this);

        mSpinner_ = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.symptoms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_.setAdapter(adapter);
        mSpinner_.setOnItemSelectedListener(this);

        mDbHandler_ = new DBHandler(SymptomLogger.this);
        signList = findViewById(R.id.sign_list);
        this.updateList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String item = (String) mSpinner_.getItemAtPosition(mSpinnerPos_);
        if (rating > 0) {
            mSymptomsMap_.put(item, (int) rating);
            Toast.makeText(this, "Symptom rating is saved !", Toast.LENGTH_LONG).show();
        } else if (mSymptomsMap_.get(item) != null) {
            mSymptomsMap_.put(item, 0);
        }
    }

    private void updateList() {

        DBHandler.SymptomData dbSymptomsList = mDbHandler_.getByID(mRecordNumber_);
        // Add in the mSpinner_ list
        mSymptomsMap_.put(NAUSEA, dbSymptomsList.getNAUSEA());
        mSymptomsMap_.put(HEADACHE, dbSymptomsList.getHEAD_ACHE());
        mSymptomsMap_.put(DIARRHEA, dbSymptomsList.getDIARRHEA());
        mSymptomsMap_.put(SOAR_THROAT, dbSymptomsList.getSOAR_THROAT());
        mSymptomsMap_.put(FEVER, dbSymptomsList.getFEVER());
        mSymptomsMap_.put(MUSCLE_ACHE, dbSymptomsList.getMUSCLE_ACHE());
        mSymptomsMap_.put(LOSS_OF_SMELL_OR_TASTE, dbSymptomsList.getNO_SMELL_TASTE());
        mSymptomsMap_.put(COUGH, dbSymptomsList.getCOUGH());
        mSymptomsMap_.put(SHORTNESS_OF_BREATH, dbSymptomsList.getSHORT_BREATH());
        mSymptomsMap_.put(FEELING_TIRED, dbSymptomsList.getFEEL_TIRED());

        // Show the list
        List<String> items = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : mSymptomsMap_.entrySet()) {
            String symptom = entry.getKey();
            Integer rating = entry.getValue();
            items.add(symptom + " " + rating);
        }
        ArrayAdapter symptomsList = new ArrayAdapter<String>(SymptomLogger.this, android.R.layout.simple_list_item_1, items);
        signList.setAdapter(symptomsList);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSpinnerPos_ = position;
        String item = (String) parent.getSelectedItem();
        if (mSymptomsMap_.get(item) != null) {
            mRatingBar_.setRating(mSymptomsMap_.get(item));
        } else {
            mRatingBar_.setRating(0);
        }
    }

    public void uploadSymptoms(View view) {
        DBHandler.SymptomData symptomModel = mDbHandler_.getByID(mRecordNumber_);
        symptomModel.setNAUSEA(mSymptomsMap_.get(NAUSEA));
        symptomModel.setHEAD_ACHE(mSymptomsMap_.get(HEADACHE));
        symptomModel.setDIARRHEA(mSymptomsMap_.get(DIARRHEA));
        symptomModel.setSOAR_THROAT(mSymptomsMap_.get(SOAR_THROAT));
        symptomModel.setFEVER(mSymptomsMap_.get(FEVER));
        symptomModel.setMUSCLE_ACHE(mSymptomsMap_.get(MUSCLE_ACHE));
        symptomModel.setNO_SMELL_TASTE(mSymptomsMap_.get(LOSS_OF_SMELL_OR_TASTE));
        symptomModel.setCOUGH(mSymptomsMap_.get(COUGH));
        symptomModel.setSHORT_BREATH(mSymptomsMap_.get(SHORTNESS_OF_BREATH));
        symptomModel.setFEEL_TIRED(mSymptomsMap_.get(FEELING_TIRED));
        mDbHandler_.updateRecords(symptomModel, mRecordNumber_);
        Toast.makeText(this, "Data saved to DB !! Record " + mRecordNumber_, Toast.LENGTH_LONG).show();
        this.updateList();
    }




}
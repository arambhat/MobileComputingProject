package com.example.covidSymptomTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {

    public static final String COVID_TABLE_NAME = "RAMBHATLA";
    public static final String ID = "ID";
    public static final String RESP_RATE = "RESPIRATION_RATE";
    public static final String HEART_RATE = "HEART_RATE";
    public static final String NAUSEA = "NAUSEA";
    public static final String HEAD_ACHE = "HEAD_ACHE";
    public static final String DIARRHEA = "DIARRHEA";
    public static final String SOAR_THROAT = "SOAR_THROAT";
    public static final String FEVER = "FEVER";
    public static final String MUSCLE_ACHE = "MUSCLE_ACHE";
    public static final String NO_SMELL_TASTE = "NO_SMELL_TASTE";
    public static final String COUGH = "COUGH";
    public static final String SHORT_BREATH = "SHORT_BREATH";
    public static final String FEEL_TIRED = "FEEL_TIRED";

    public DBHandler(@Nullable Context context) {
        super(context, "rambhatla.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + COVID_TABLE_NAME + "( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + HEART_RATE + " INTEGER, "
                + RESP_RATE + " INTEGER, "
                + NAUSEA + " INTEGER, "
                + HEAD_ACHE + " INTEGER, "
                + DIARRHEA + " INTEGER, "
                + SOAR_THROAT + " INTEGER, "
                + FEVER + " INTEGER, "
                + MUSCLE_ACHE + " INTEGER, "
                + NO_SMELL_TASTE + " INTEGER, "
                + COUGH + " INTEGER, "
                + SHORT_BREATH + " INTEGER, "
                + FEEL_TIRED + " INTEGER" + " )";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getRecordCount() {
        int count;

        String query = "SELECT COUNT(*) AS COUNT FROM " + COVID_TABLE_NAME;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery(query, null);
            cur.moveToFirst();
            count = cur.getInt(0);
        } catch (Exception e) {
            return 0;
        }
        return count;
    }

    public boolean updateRecords(SymptomData model, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(HEART_RATE, model.getHEART_RATE());
        cv.put(RESP_RATE, model.getRESP_RATE());
        cv.put(NAUSEA, model.getNAUSEA());
        cv.put(HEAD_ACHE, model.getHEAD_ACHE());
        cv.put(DIARRHEA, model.getDIARRHEA());
        cv.put(SOAR_THROAT, model.getSOAR_THROAT());
        cv.put(FEVER, model.getFEVER());
        cv.put(MUSCLE_ACHE, model.getMUSCLE_ACHE());
        cv.put(NO_SMELL_TASTE, model.getNO_SMELL_TASTE());
        cv.put(COUGH, model.getCOUGH());
        cv.put(SHORT_BREATH, model.getSHORT_BREATH());
        cv.put(FEEL_TIRED, model.getFEEL_TIRED());

        //Insert
        long insert = db.insertWithOnConflict(COVID_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (insert == -1) {
            insert = db.update(COVID_TABLE_NAME, cv, ID + "=?", new String[]{String.valueOf(id)});
        }
        db.close();
        return insert != -1;
    }

    public SymptomData getByID(int id) {
        SymptomData symptom = new SymptomData();
        String query = "SELECT * FROM " + COVID_TABLE_NAME + " WHERE " + ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            symptom.setRESP_RATE(cursor.getInt(1));
            symptom.setHEART_RATE(cursor.getInt(2));
            symptom.setNAUSEA(cursor.getInt(3));
            symptom.setHEAD_ACHE(cursor.getInt(4));
            symptom.setDIARRHEA(cursor.getInt(5));
            symptom.setSOAR_THROAT(cursor.getInt(6));
            symptom.setFEVER(cursor.getInt(7));
            symptom.setMUSCLE_ACHE(cursor.getInt(8));
            symptom.setNO_SMELL_TASTE(cursor.getInt(9));
            symptom.setCOUGH(cursor.getInt(10));
            symptom.setSHORT_BREATH(cursor.getInt(11));
            symptom.setFEEL_TIRED(cursor.getInt(12));
        }
        cursor.close();
        db.close();
        return symptom;
    }

    public static class SymptomData {
        private int RESP_RATE;
        private int HEART_RATE ;
        private int NAUSEA ;
        private int HEAD_ACHE;
        private int DIARRHEA;
        private int SOAR_THROAT;
        private int FEVER ;
        private int MUSCLE_ACHE ;
        private int NO_SMELL_TASTE ;
        private int COUGH ;
        private int SHORT_BREATH;
        private int FEEL_TIRED;

        public SymptomData() {
            this.RESP_RATE = 0;
            this.HEART_RATE = 0;
            this.NAUSEA = 0;
            this.HEAD_ACHE = 0;
            this.DIARRHEA = 0;
            this.SOAR_THROAT = 0;
            this.FEVER = 0;
            this.MUSCLE_ACHE = 0;
            this.NO_SMELL_TASTE = 0;
            this.COUGH = 0;
            this.SHORT_BREATH = 0;
            this.FEEL_TIRED = 0;
        }


        @Override
        public String toString() {
            return "SymptomData{" +
                    "RESP_RATE=" + RESP_RATE +
                    ", HEART_RATE=" + HEART_RATE +
                    ", NAUSEA=" + NAUSEA +
                    ", HEAD_ACHE=" + HEAD_ACHE +
                    ", DIARRHEA=" + DIARRHEA +
                    ", SOAR_THROAT=" + SOAR_THROAT +
                    ", FEVER=" + FEVER +
                    ", MUSCLE_ACHE=" + MUSCLE_ACHE +
                    ", NO_SMELL_TASTE=" + NO_SMELL_TASTE +
                    ", COUGH=" + COUGH +
                    ", SHORT_BREATH=" + SHORT_BREATH +
                    ", FEEL_TIRED=" + FEEL_TIRED +
                    '}';
        }

        public int getRESP_RATE() {
            return RESP_RATE;
        }

        public void setRESP_RATE(int RESP_RATE) {
            this.RESP_RATE = RESP_RATE;
        }

        public int getHEART_RATE() {
            return HEART_RATE;
        }

        public void setHEART_RATE(int HEART_RATE) {
            this.HEART_RATE = HEART_RATE;
        }

        public int getNAUSEA() {
            return NAUSEA;
        }

        public void setNAUSEA(int NAUSEA) {
            this.NAUSEA = NAUSEA;
        }

        public int getHEAD_ACHE() {
            return HEAD_ACHE;
        }

        public void setHEAD_ACHE(int HEAD_ACHE) {
            this.HEAD_ACHE = HEAD_ACHE;
        }

        public int getDIARRHEA() {
            return DIARRHEA;
        }

        public void setDIARRHEA(int DIARRHEA) {
            this.DIARRHEA = DIARRHEA;
        }

        public int getSOAR_THROAT() {
            return SOAR_THROAT;
        }

        public void setSOAR_THROAT(int SOAR_THROAT) {
            this.SOAR_THROAT = SOAR_THROAT;
        }

        public int getFEVER() {
            return FEVER;
        }

        public void setFEVER(int FEVER) {
            this.FEVER = FEVER;
        }

        public int getMUSCLE_ACHE() {
            return MUSCLE_ACHE;
        }

        public void setMUSCLE_ACHE(int MUSCLE_ACHE) {
            this.MUSCLE_ACHE = MUSCLE_ACHE;
        }

        public int getNO_SMELL_TASTE() {
            return NO_SMELL_TASTE;
        }

        public void setNO_SMELL_TASTE(int NO_SMELL_TASTE) {
            this.NO_SMELL_TASTE = NO_SMELL_TASTE;
        }

        public int getCOUGH() {
            return COUGH;
        }

        public void setCOUGH(int COUGH) {
            this.COUGH = COUGH;
        }

        public int getSHORT_BREATH() {
            return SHORT_BREATH;
        }

        public void setSHORT_BREATH(int SHORT_BREATH) {
            this.SHORT_BREATH = SHORT_BREATH;
        }

        public int getFEEL_TIRED() {
            return FEEL_TIRED;
        }

        public void setFEEL_TIRED(int FEEL_TIRED) {
            this.FEEL_TIRED = FEEL_TIRED;
        }
    }
}

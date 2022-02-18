package com.example.smarthomegesturetrainer.ui.Practice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PracticeViewModel extends ViewModel {

    public String videoName;
    public String baseUrl = "http://192.168.0.214:5000/";


    public String getBaseUrl() {
        return baseUrl;
    }
    public String getVideoNameFromGesture(String gesture) {
        if (gesture.equals("Turn on lights")) {
            videoName="LightOn";
        } else if (gesture.equals("Turn off lights")) {
            videoName = "LightOff";
        } else if (gesture.equals("Turn on fan")) {
            videoName= "FanOn";;
        } else if (gesture.equals("Turn off fan")) {
            videoName= "FanOff";
        } else if (gesture.equals("Increase fan speed")) {
            videoName= "FanUp";
        } else if (gesture.equals("Decrease fan speed")) {
            videoName= "FanDown";
        } else if (gesture.equals("Set Thermostat to temperature")) {
            videoName="SetThermo";
        } else if (gesture.equals("Number 0")) {
            videoName= "Num0";
        } else if (gesture.equals("Number 1")) {
            videoName= "Num1";
        } else if (gesture.equals("Number 2")) {
            videoName= "Num2";
        } else if (gesture.equals("Number 3")) {
            videoName= "Num3";
        } else if (gesture.equals("Number 4")) {
            videoName= "Num4";
        } else if (gesture.equals("Number 5")) {
            videoName= "Num5";
        } else if (gesture.equals("Number 6")) {
            videoName= "Num6";
        } else if (gesture.equals("Number 7")) {
            videoName= "Num7";
        } else if (gesture.equals("Number 8")) {
            videoName= "Num8";
        } else if (gesture.equals("Number 9")) {
            videoName= "Num9";
        }
        return videoName;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getPracticeVideoName(int practiceNum) {return videoName + "_PRACTICE_" + practiceNum + "Rambhatla.mp4";}
}
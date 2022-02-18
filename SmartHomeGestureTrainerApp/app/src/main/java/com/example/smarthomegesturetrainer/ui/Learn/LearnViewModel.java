package com.example.smarthomegesturetrainer.ui.Learn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LearnViewModel extends ViewModel {

    public String videoName;

    public String getVideoNameFromGesture(String gesture) {
        if (gesture.equals("Turn on lights")) {
            videoName="H-LightOn.mp4";
        } else if (gesture.equals("Turn off lights")) {
            videoName = "H-LightOff.mp4";
        } else if (gesture.equals("Turn on fan")) {
            videoName= "H-FanOn.mp4";;
        } else if (gesture.equals("Turn off fan")) {
            videoName= "H-FanOff.mp4";
        } else if (gesture.equals("Increase fan speed")) {
            videoName= "H-IncreaseFanSpeed.mp4";
        } else if (gesture.equals("Decrease fan speed")) {
            videoName= "H-DecreaseFanSpeed.mp4";
        } else if (gesture.equals("Set Thermostat to temperature")) {
            videoName="H-SetThermo.mp4";
        } else if (gesture.equals("Number 0")) {
            videoName= "H-0.mp4";
        } else if (gesture.equals("Number 1")) {
            videoName= "H-1.mp4";
        } else if (gesture.equals("Number 2")) {
            videoName= "H-2.mp4";
        } else if (gesture.equals("Number 3")) {
            videoName= "H-3.mp4";
        } else if (gesture.equals("Number 4")) {
            videoName= "H-4.mp4";
        } else if (gesture.equals("Number 5")) {
            videoName= "H-5.mp4";
        } else if (gesture.equals("Number 6")) {
            videoName= "H-6.mp4";
        } else if (gesture.equals("Number 7")) {
            videoName= "H-7.mp4";
        } else if (gesture.equals("Number 8")) {
            videoName= "H-8.mp4";
        } else if (gesture.equals("Number 9")) {
            videoName= "H-9.mp4";
        }
        return videoName;
    }

    public String getVideoName() {
        return videoName;
    }
}
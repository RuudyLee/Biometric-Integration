package com.example.dashboard;

import android.graphics.Color;

/**
 * Created by Colin on 3/18/2017.
 */



public abstract class Alert {

    int m_background;
    String m_message;

    public void Initalize(int backgroundColor, String message){
        m_background = backgroundColor;
        m_message = message;
    }

    public abstract boolean CheckCondition();

}

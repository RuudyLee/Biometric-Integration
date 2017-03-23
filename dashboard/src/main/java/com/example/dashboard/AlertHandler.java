package com.example.dashboard;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Colin on 3/18/2017.
 *
 * Used to handle notifications with different conditions for triggering.
 * stores a list of different notifications derrived from baseNotification.
 */

public class AlertHandler extends Fragment {

    ArrayList<Alert> m_notes;
    TextView m_Message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        return inflater.inflate(R.layout.fragment_alert,container,false);

    }

    //Adds notification to list
    public void AddAlert(Alert instance){
        m_notes.add(instance);
    }

    //Checks each notification to see if its condition for trigger is true.
    public boolean CheckNotifications(){

        int index = 0; //Not switch from iterator to index
        for(Iterator<Alert> i = m_notes.iterator(); i.hasNext();)
        {
            Alert note = i.next();
            index++;
            if(note.CheckCondition())
            {
                TriggerAlert(index);
                return true;
            }

        }

        return false;
    }

    //Pushed notifications to the screen
    public void TriggerAlert(int index){

        m_Message = (TextView)getView().findViewById(R.id.message);
        m_Message.setText(m_notes.get(index).m_message);
        getView().setBackgroundColor(m_notes.get(index).m_background);

    }
}

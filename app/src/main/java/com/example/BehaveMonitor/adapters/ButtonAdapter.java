package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.BehaveMonitor.Behaviour;
import com.example.BehaveMonitor.BehaviourType;
import com.example.BehaveMonitor.Event;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.SessionActivity;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ButtonAdapter extends BaseAdapter {

//    private Context context;
    private LayoutInflater inflater;

    private SessionActivity sessionActivity;
    private HistoryAdapter historyAdapter;
    private List<Behaviour> behaviours;

    final Handler myHandler;
    private Timer timer;

    private Button activeButton;
    private Behaviour activeBehaviour;

    private boolean started = false;

    public ButtonAdapter(Context context, HistoryAdapter historyAdapter, List<Behaviour> behaviours, Timer timer, Handler handler, SessionActivity sessionActivity) {
//        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.sessionActivity = sessionActivity;
        this.historyAdapter = historyAdapter;
        this.behaviours = behaviours;

        this.timer = timer;
        this.myHandler = handler;
    }

    public void endSession() {
        if (activeBehaviour != null) deactivateBehaviour();
    }

    @Override
    public int getCount() {
        return behaviours.size();
    }

    @Override
    public Object getItem(int position) {
        return behaviours.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Button button;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.behaviour_button, parent, false);
            button = (Button) convertView;
            convertView.setTag(button);
        } else {
            button = (Button) convertView.getTag();
        }

        final Behaviour behaviour = behaviours.get(position);
        button.setText(behaviour.getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started && !sessionActivity.isStarted()) {
                    sessionActivity.startSession();
                }

                started = true;

                if (behaviour.getType() == BehaviourType.EVENT) { // An event, simply activate it.
                    behaviour.newEvent();
                    addToHistory(behaviour, false);
                } else {
                    if (activeButton == null) {
                        activateBehaviour(behaviour, button);
                    } else {
                        if (activeBehaviour.getName().equals(behaviour.getName())) {
                            deactivateBehaviour();
                        } else {
                            deactivateBehaviour();
                            activateBehaviour(behaviour, button);
                        }
                    }
                }
            }
        });

        return convertView;
    }

    private void activateBehaviour(Behaviour b, Button button) {
        b.newEvent();
        activeBehaviour = b;
        button.setTextColor(Color.parseColor("#99CC00"));
        activeButton = button;
        //Add the update button task to the timer.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateButtonTime();
            }
        }, 0, 100);
    }

    private void deactivateBehaviour() {
        //removes any button update tasks.
        timer.purge();

        activeBehaviour.endCurrentEvent();
        addToHistory(activeBehaviour, true);
        //reset the button to its name
        activeButton.setText(activeBehaviour.getName());
        activeButton.setTextColor(Color.parseColor("#ffffff"));

        activeButton = null;
        activeBehaviour = null;
    }

    private void addToHistory(Behaviour behaviour, boolean state) {
        Event event = behaviour.getLastEvent();

        String message = behaviour.getName();
        if (state) {
            message += "          " + event.getDuration() + "s";
        }

        historyAdapter.addEvent(event, message);
    }

    private void UpdateButtonTime() {
        myHandler.post(updateButtonTime);
    }

    final Runnable updateButtonTime = new Runnable() {
        @Override
        public void run() {
            if (activeButton != null) {
                //Gets the time the behaviour started from the current event on that behaviour.
                activeButton.setText(activeBehaviour.getName() + "\n" + timeDiff(activeBehaviour.getCurrentEvent().getStartTime(), new Date()));
            }
        }
    };

    public String timeDiff(Date sT, Date eT) {

        long diff = eT.getTime() - sT.getTime();
        int seconds = (int) diff / 1000;
        diff -= seconds * 1000;

        String out = seconds + ".";
        int length = ("" + diff).length();

        switch(length) {
            case(0):
                out += "000";
                break;
            case(1):
                out += "00" + diff;
                break;
            case(2):
                out += "0" + diff;
                break;
            case(3):
                out += "" + diff;
                break;
            default:
                break;
        }

        return out;
    }

//    private void makeSomeToast(final String message) {
//        final Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
//        toast.show();
//    }
}

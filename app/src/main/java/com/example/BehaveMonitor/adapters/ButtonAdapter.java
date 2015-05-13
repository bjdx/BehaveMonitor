//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.BehaveMonitor.Behaviour;
import com.example.BehaveMonitor.BehaviourType;
import com.example.BehaveMonitor.Event;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.SessionActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ButtonAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private SessionActivity sessionActivity;
    private HistoryAdapter historyAdapter;
    private List<Behaviour> behaviours;

    final Handler myHandler;
    private Timer timer;

    private List<Integer> activeBehaviours;
    private Button[] buttons;
//    private Button activeButton;
//    private Behaviour activeBehaviour;

    private GridView parent;

    private boolean started = false;

    public ButtonAdapter(Context context, HistoryAdapter historyAdapter, List<Behaviour> behaviours, Timer timer, Handler handler, SessionActivity sessionActivity, GridView parent) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.sessionActivity = sessionActivity;
        this.historyAdapter = historyAdapter;
        this.behaviours = behaviours;

        this.timer = timer;
        this.myHandler = handler;

//        this.times = new String[behaviours.size()];
        this.activeBehaviours = new ArrayList<>(behaviours.size());

        this.parent = parent;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateButtonTime();
            }
        }, 0, (1000 / 30));
    }

    public void endSession() {
//        if (activeBehaviour != null) deactivateBehaviour();
        for (int i = 0; i < behaviours.size(); i++) {
            Behaviour behaviour = behaviours.get(i);
            if (behaviour.isActive()) {
                deactivateBehaviour(behaviour, i);
            }
        }

        timer.cancel();
        timer.purge();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Button button;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.behaviour_button, parent, false);
            button = (Button) convertView;
            convertView.setTag(button);
        } else {
//            Log.e("Behave", "Recycling view");
            button = (Button) convertView.getTag();
        }

        final Behaviour behaviour = behaviours.get(position);

//        if (behaviour.isActive()) {
//            button.setText(behaviour.getName() + "\n" + times[position]);
//            button.setTextColor(Color.parseColor("#99CC00"));
//        } else {
        if (!behaviour.isActive()) {
            button.setText(behaviour.getName());
            button.setTextColor(Color.parseColor("#ffffff"));
        }
//        }

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
                    if (!behaviour.isActive()) {
                        activateBehaviour(behaviour, position);
                    } else {
                        deactivateBehaviour(behaviour, position);
                    }


//                    if (activeButton == null) {
//                        activateBehaviour(behaviour, button);
//                    } else {
//                        if (activeBehaviour.getName().equals(behaviour.getName())) {
//                            deactivateBehaviour();
//                        } else {
//                            deactivateBehaviour();
//                            activateBehaviour(behaviour, button);
//                        }
//                    }
                }
            }
        });

        return convertView;
    }

    private void activateBehaviour(Behaviour behaviour, int position) {
        behaviour.newEvent();
        activeBehaviours.add(position);

//        Log.e("Behave", "First position: " + parent.getFirstVisiblePosition());
    }

//    private void activateBehaviour(Behaviour b, Button button) {
//        b.newEvent();
//        activeBehaviour = b;
//        button.setTextColor(Color.parseColor("#99CC00"));
////        button.setTextColor(Color.parseColor("#2E7A33"));
//        activeButton = button;
//        //Add the update button task to the timer.
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                UpdateButtonTime();
//            }
//        }, 0, 100);
//    }

    private void deactivateBehaviour(Behaviour behaviour, int position) {
        behaviour.endCurrentEvent();
        addToHistory(behaviour, true);

        activeBehaviours.remove(Integer.valueOf(position)); // This removes the first occurrence of position, rather than the element at index 'position'
        notifyDataSetChanged();
    }

//    private void deactivateBehaviour() {
//        //removes any button update tasks.
////        timer.purge();
//
//        activeBehaviour.endCurrentEvent();
//        addToHistory(activeBehaviour, true);
//        //reset the button to its name
//        activeButton.setText(activeBehaviour.getName());
//        activeButton.setTextColor(Color.parseColor("#ffffff"));
//
//        activeButton = null;
//        activeBehaviour = null;
//    }

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
            if (activeBehaviours.size() > 0) {
                for (int position : activeBehaviours) {
                    Behaviour behaviour = behaviours.get(position);
                    Button button = (Button) parent.getChildAt(position - parent.getFirstVisiblePosition());
                    if (button != null) {
                        button.setText(behaviour.getName() + "\n" + timeDiff(behaviour.getCurrentEvent().getStartTime(), new Date()));
                        button.setTextColor(Color.parseColor("#99CC00"));
                    }
//                    buttons[position].setText(behaviour.getName() + "\n" + timeDiff(behaviour.getCurrentEvent().getStartTime(), new Date()));
                }

//                notifyDataSetChanged();
            }
        }
    };

//    final Runnable updateButtonTime = new Runnable() {
//        @Override
//        public void run() {
//            if (activeButton != null) {
//                //Gets the time the behaviour started from the current event on that behaviour.
//                activeButton.setText(activeBehaviour.getName() + "\n" + timeDiff(activeBehaviour.getCurrentEvent().getStartTime(), new Date()));
//            }
//        }
//    };

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

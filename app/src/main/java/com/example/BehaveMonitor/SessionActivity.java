package com.example.BehaveMonitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SessionActivity extends Activity {

    Session activeSession;
    TextView sessionTimeTV = null;
    final Handler myHandler = new Handler();
    Behaviour activeBehaviour = null;
    Button activeButton = null;
    Timer bTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        Bundle b = getIntent().getExtras();
        activeSession = b.getParcelable("Session");
        if(activeSession != null) Log.e("Active Session is null","Parcel didnt work"+activeSession.name);
        setupTimer();
        loadSessionInfo();
        setStartButton();
    }

    private void setupTimer() {
        bTimer = new Timer();
        final int delay = 100;

    }

    private void loadSessionInfo() {
        //Sets the text of the textview to correct Session details
        TextView name = (TextView) findViewById(R.id.sNameText);
        TextView location = (TextView) findViewById(R.id.sLocText);
        TextView tmpText = (TextView) findViewById(R.id.sTmpText);
        name.setText(name.getText().toString()+"  "+activeSession.name);
        location.setText(location.getText().toString()+"  "+activeSession.location);
        tmpText.setText(tmpText.getText().toString()+"  "+activeSession.template.name);

        //Creates list of behaviours
        LinearLayout ll = (LinearLayout) findViewById(R.id.behaveLayout);
        TextView[] bTV = new TextView[activeSession.template.behaviours.size()];
        int i = 0;
        for(Behaviour b:activeSession.template.behaviours){
            bTV[i] = new TextView(this);
            bTV[i].setText(b.bName);
            ll.addView(bTV[i]);
            i++;
        }

    }

    public void setStartButton() {
        Button button = (Button) findViewById(R.id.start_session);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSession();
            }
        });

    }

    private void UpdateGUI() {
        //tv.setText(String.valueOf(i)); //This causes a runtime error.
        myHandler.post(updateTime);
    }

    final Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            sessionTimeTV.setText("Session Time: "+ activeSession.getRelativeHMS(new Date()));
        }
    };


    private void addSessionTimerTask() {
        final Timer myTimer = new Timer();
        final int delay = 100;
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI();
            }
        }, 0, delay);
    }

    //Called to reconfigure the screen for button display and hides the session information
    public void startSession() {
        //hide the session info layout
        LinearLayout ll = (LinearLayout) findViewById(R.id.info_layout);
        ll.setVisibility(View.GONE);

        //get the main layout
        LinearLayout sml = (LinearLayout) findViewById(R.id.session_main_layout);

        //Add current session time to the top of the screen
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //17 is CENTER gravity.
        tv.setGravity(17);
        sessionTimeTV = tv;
        addSessionTimerTask();




        int index = 0;
        LinearLayout currentLevel = null;

        //Go through the Behaviours and create the button layout
        for(Behaviour b : activeSession.template.behaviours) {
            //if its the first behaviour in a block of three create a new layout.
            if(index % 3 == 0) {
                //check to see its not the first behaviour (currentLevel hasn't been created).
                if(currentLevel != null) sml.addView(currentLevel);
                currentLevel = new LinearLayout(this);
                currentLevel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Button button = createBehaviourButton(b);
                currentLevel.addView(button);
            } else {
                Button button = createBehaviourButton(b);
                if(currentLevel != null) currentLevel.addView(button);
            }
            index++;
        }

        //If there is less than 3 buttons in the final row, it won't have been added so add it.
        if(index % 3 != 0) {
            sml.addView(currentLevel);
        }

    }

    // create the button for the sessions layout
    public Button createBehaviourButton(final Behaviour b) {
        Button button = new Button(this);
        //set the buttons name to the behaviour name
        button.setText(b.bName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;

                //Add a new event to the behaviour


                //If its a state activate it
                //If its an event behaviour just add a new Event.
                if (b.type == 1) {
                    //No active behaviour start a new one
                    if(activeButton == null) {
                        activateBehaviour(b,button);

                    //there is a current event - must be deactivated.
                    } else {
                        //If the behaviour is active end it
                        if(activeBehaviour.bName.equals(b.bName)) {
                            deactivateBehaviour(b,button);

                        //If its a new behaviour, end the old one and start the new one.
                        } else {
                            deactivateBehaviour(activeBehaviour,activeButton);
                            activateBehaviour(b,button);

                        }
                    }
                } else {
                    b.newEvent();
                    makeSomeToast(b.bName +" Event added.");
                }
            }
        });
        return button;
    }

    private void activateBehaviour(Behaviour b, Button button) {
        b.newEvent();
        activeBehaviour = b;
        button.setTextColor(Color.parseColor("#99CC00"));
        activeButton = button;
        //Add the update button task to the timer.
        bTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateButtonTime();
            }
        }, 0, 100);
    }

    private void deactivateBehaviour(Behaviour b, Button button) {
        //removes any button update tasks.
        bTimer.purge();

        b.endCurrentEvent();
        //reset the button to its name
        activeButton.setText(b.bName);
        button.setTextColor(Color.parseColor("#000000"));
        //makeSomeToast(b.bName + " " + b.getLastEvent().duration);
        activeButton = null;
        activeBehaviour = null;
    }

    private void UpdateButtonTime() {
        myHandler.post(updateButtonTime);
    }

    final Runnable updateButtonTime = new Runnable() {
        @Override
        public void run() {
            if (activeButton != null) {
                //Sets the time to: behaviourName
                //                    SS.sss

               //Gets the time the behaviour started from the current event on that behaviour.
                activeButton.setText(activeBehaviour.bName + "\n" + activeSession.timeDiff(activeBehaviour.currentEvent.startTime,new Date()));
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeSomeToast(final String message) {
        final Context context = getApplicationContext();
        final CharSequence text = message;
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}

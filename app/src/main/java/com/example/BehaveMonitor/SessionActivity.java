package com.example.BehaveMonitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SessionActivity extends Activity {

   Session activeSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        Bundle b = getIntent().getExtras();
        activeSession = b.getParcelable("Session");
        if(activeSession != null) Log.e("Active Session is null","Parcel didnt work"+activeSession.name);
        loadSessionInfo();
        setStartButton();
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

    //Called to reconfigure the screen for button display and hides the session information
    public void startSession() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.info_layout);
        ll.setVisibility(View.GONE);
        LinearLayout sml = (LinearLayout) findViewById(R.id.session_main_layout);
        int index = 0;
        LinearLayout currentLevel;
        for(Behaviour b : activeSession.template.behaviours) {
            if(index == 0) {
                currentLevel = new LinearLayout(this);
                Button button = createBehaviourButton(b);
            } else if(index % 3 == 0) {
                currentLevel = new LinearLayout(this);
            } else {

            }
        }

    }

    public Button createBehaviourButton(final Behaviour b) {
        Button button = new Button(this);
        button.setText(b.bName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                b.newEvent(b.type);
                //If its a state add the timer and change the colour
                if (b.type == 1) {
                    button.setTextColor(Color.parseColor("#99CC00"));
                }
            }
        });
        return button;
    }

    public void activateStateB() {

    }

    public void activateEventB() {

    }


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

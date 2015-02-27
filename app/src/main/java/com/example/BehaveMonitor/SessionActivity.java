package com.example.BehaveMonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BehaveMonitor.adapters.ButtonAdapter;
import com.example.BehaveMonitor.adapters.HistoryAdapter;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


public class SessionActivity extends Activity {

    Session activeSession;
    TextView sessionTimeTV = null;
    final Handler myHandler = new Handler();
    Timer bTimer = null;

    private String activeFolder;
    private ButtonAdapter adapter;
    private HistoryAdapter historyAdapter;

    private boolean sessionStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Bundle b = getIntent().getExtras();
        activeSession = b.getParcelable("Session");
        activeFolder = b.getString("activeFolderString");

//        if(activeSession != null) Log.e("Active Session is null","Parcel didnt work"+activeSession.name);
        setupTimer();
        loadSessionInfo();
        setStartButton();
    }

    private void setupTimer() {
        bTimer = new Timer();
//        final int delay = 100;

    }

    private void loadSessionInfo() {
        //Sets the text of the textview to correct Session details
        TextView name = (TextView) findViewById(R.id.sNameText);
        TextView location = (TextView) findViewById(R.id.sLocText);
        TextView tmpText = (TextView) findViewById(R.id.sTmpText);
        name.setText(name.getText().toString()+"  "+activeSession.getName());
        location.setText(location.getText().toString()+"  "+activeSession.getLocation());
        tmpText.setText(tmpText.getText().toString()+"  "+activeSession.getTemplate().name);

        //Creates list of behaviours
        LinearLayout ll = (LinearLayout) findViewById(R.id.behaveLayout);
        TextView[] bTV = new TextView[activeSession.getTemplate().behaviours.size()];
        int i = 0;
        for(Behaviour b:activeSession.getTemplate().behaviours){
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
            sessionTimeTV.setText(activeSession.getRelativeHMS(new Date()));
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
        findViewById(R.id.info_layout).setVisibility(View.GONE);

        activeSession.startSession();

        sessionTimeTV = (TextView) findViewById(R.id.session_time);
        addSessionTimerTask();

        setupListView();
        setupGridView();

        sessionStarted = true;
    }

    public void setupListView() {
        ListView list = (ListView) findViewById(R.id.session_history_list);
        historyAdapter = new HistoryAdapter(this, new LinkedList<String>());
        list.setAdapter(historyAdapter);
    }

    public void setupGridView() {
        GridView grid = (GridView) findViewById(R.id.session_behaviour_grid);
        adapter = new ButtonAdapter(this, historyAdapter, activeSession.getTemplate().behaviours, bTimer, myHandler);
        grid.setAdapter(adapter);
    }

    public void endSession(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("End Session?");
        dialog.setMessage("Are you sure you want to end the session?");

        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.endSession();
                activeSession.endSession();

                boolean saved = FileHandler.saveSession(activeFolder, activeSession);
                if (saved) {
                    makeSomeToast("File saved.");
                    Intent intent = new Intent(SessionActivity.this, HomeActivity.class);
                    intent.putExtra("activeFolderString", new File(FileHandler.getSessionsDirectory(), activeFolder).getAbsolutePath());

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    makeSomeToast("Error when saving.");
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (sessionStarted) {
            endSession(null);
        } else {
            Intent intent = new Intent(SessionActivity.this, HomeActivity.class);
            intent.putExtra("activeFolderString", new File(FileHandler.getSessionsDirectory(), activeFolder).getAbsolutePath());

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    private void makeSomeToast(final String message) {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

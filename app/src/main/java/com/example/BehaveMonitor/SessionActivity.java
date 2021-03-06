//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BehaveMonitor.adapters.ButtonAdapter;
import com.example.BehaveMonitor.adapters.HistoryAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class SessionActivity extends Activity {

    Session activeSession;
    TextView sessionTimeTV = null;
    final Handler myHandler = new Handler();
    Timer bTimer = null;

    private int observation = 1;
    private String activeFolder;
    private ButtonAdapter adapter;
    private HistoryAdapter historyAdapter;

    private String namePrefix = "";
    private String filename = "";
    private boolean sessionStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Bundle b = getIntent().getExtras();
        activeSession = b.getParcelable("Session");
        activeFolder = b.getString("activeFolderString");
        namePrefix = activeSession.getName().trim();

        loadSessionInfo();
        setSetupButton();
        setStartButton();
    }

    private void loadSessionInfo() {
        // Populates text views with session details
        ((TextView) findViewById(R.id.sNameText)).setText("Name:  " + activeSession.getName());
        ((TextView) findViewById(R.id.sLocText)).setText("Location:  " + activeSession.getLocation());
        ((TextView) findViewById(R.id.setup_observations_text)).setText("Observations:  " + activeSession.getObservationsCount());
        ((TextView) findViewById(R.id.sTmpText)).setText("Template:  " + activeSession.getTemplate(observation).name);

        // Creates list of behaviours
        List<Behaviour> behaviours = activeSession.getBehaviours(1);
        List<String> behaviourNames = new ArrayList<>(behaviours.size());

        for (Behaviour behaviour : behaviours) {
            behaviourNames.add(behaviour.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.session_behaviour_list_item, behaviourNames);
        ListView list = (ListView) findViewById(R.id.session_behaviour_list);
        list.setAdapter(adapter);

//        LinearLayout ll = (LinearLayout) findViewById(R.id.behaveLayout);
//        TextView[] bTV = new TextView[activeSession.getTemplate(observation).behaviours.size()];
//        int i = 0;
//        for (Behaviour b : activeSession.getTemplate(observation).behaviours){
//            bTV[i] = new TextView(this);
//            bTV[i].setText(b.bName);
//            ll.addView(bTV[i]);
//            i++;
//        }
    }

    public void setSetupButton() {
        Button button = (Button) findViewById(R.id.setup_session);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSession();
            }
        });
    }

    public void setStartButton() {
        Button button = (Button) findViewById(R.id.start_session_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSession();
            }
        });
    }

    private void UpdateGUI() {
        myHandler.post(updateTime);
    }

    final Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            sessionTimeTV.setText(activeSession.getRelativeHMS(observation));
        }
    };

    private void addSessionTimerTask() {
        bTimer = new Timer();
        final int delay = 1000; // Run once a second, since the time is not recorded to a higher precision, there is no point running more frequently.
        bTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI();
            }
        }, 0, delay);
    }

    private void addAutosaveTimerTask() {
        if (bTimer == null) {
            bTimer = new Timer();
        }

        bTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                autosave();
            }
        }, 60000, 60000);
    }

    private void autosave() {
        boolean saved = FileHandler.saveSession(activeFolder, activeSession, filename, observation);
        if (saved) {
            myHandler.post(autosaveSuccess);
        } else {
            myHandler.post(autosaveFail);
        }
    }

    final Runnable autosaveSuccess = new Runnable() {
        @Override
        public void run() {
            makeSomeToast("Autosaved");
        }
    };

    final Runnable autosaveFail = new Runnable() {
        @Override
        public void run() {
            makeSomeToast("Autosave failed");
        }
    };

    /**
     * Reconfigures the screen for button display and hides the session information.
     * Also resets the session for each new observation.
     */
    public void setupSession() {
        // hide the session info layout
        if (observation == 1) {
            findViewById(R.id.info_layout).setVisibility(View.GONE);
        }

        String name = namePrefix;
        if (activeSession.getObservationsCount() > 1) {
            name += observation;
            activeSession.setName(name);
        }

        filename = FileHandler.getVersionName(activeFolder, name + "_" + activeSession.getLocation());

        if (observation > 1) {
            resetSession();
        }

        setupListView();
        setupGridView();

        ((TextView) findViewById(R.id.session_observation_name)).setText(name + "_" + activeSession.getLocation());
        ((TextView) findViewById(R.id.session_observation_number)).setText("" + observation);
    }

    private void resetSession() {
        sessionTimeTV.setText("Not Started");

        // Swap the end button for the start button.
        findViewById(R.id.start_session_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.end_session_btn).setVisibility(View.GONE);

        sessionStarted = false;
    }

    public void setupListView() {
        ListView list = (ListView) findViewById(R.id.session_history_list);
        historyAdapter = new HistoryAdapter(this, new LinkedList<String>());
        list.setAdapter(historyAdapter);
    }

    public void setupGridView() {
        GridView grid = (GridView) findViewById(R.id.session_behaviour_grid);
        adapter = new ButtonAdapter(this, historyAdapter, activeSession.getTemplate(observation).behaviours, new Timer(), myHandler, this);
        grid.setAdapter(adapter);
    }

    public boolean isStarted() {
        return sessionStarted;
    }

    public void startSession() {
        activeSession.startSession();

        sessionTimeTV = (TextView) findViewById(R.id.session_time);
        addSessionTimerTask();
        addAutosaveTimerTask();

        findViewById(R.id.start_session_btn).setVisibility(View.GONE);
        findViewById(R.id.end_session_btn).setVisibility(View.VISIBLE);

        sessionStarted = true;
    }

    public void endSession(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Finish observation?");
        dialog.setMessage("Are you sure you want to end this observation?");

        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.endSession();
                activeSession.endSession();
                bTimer.cancel();
                bTimer.purge();

                List<Integer[]> markedEvents = new ArrayList<>();
                List<Behaviour> behaviours = activeSession.getBehaviours(observation);
                for (int i = 0; i < behaviours.size(); i++) {
                    List<Event> eventHistory = behaviours.get(i).eventHistory;
                    for (int j = 0; j < eventHistory.size(); j++) {
                        if (eventHistory.get(j).getMark()) {
                            markedEvents.add(new Integer[] {i, j});
                        }
                    }
                }

                if (markedEvents.size() > 0) {
                    showNotesQuestionDialog(behaviours, markedEvents);
                } else {
                    saveSession();
                }
            }
        });

        dialog.show();
    }

    private void showNotesQuestionDialog(final List<Behaviour> behaviours, final List<Integer[]> events) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Add notes or save now?");
        dialog.setNeutralButton("Save Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSession();
            }
        });

        dialog.setPositiveButton("Add Notes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showNotesDialog(behaviours, events, 0);
            }
        });

        dialog.show();
    }

    private void showNotesDialog(List<Behaviour> behaviours, List<Integer[]> events, int position) {
        Integer[] indices = events.get(position);
        Behaviour behaviour = behaviours.get(indices[0]);
        Event event = behaviour.eventHistory.get(indices[1]);
        if (behaviour.getType() == BehaviourType.EVENT) {
            showEventDialog(behaviours, events, behaviour.bName, event, position);
        } else {
            showStateDialog(behaviours, events, behaviour.bName, event, position);
        }
    }

    private void showEventDialog(final List<Behaviour> behaviours, final List<Integer[]> events, String behaviourName, final Event event, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_note_event, null);
        dialog.setView(view);

        TextView titleText = (TextView) view.findViewById(R.id.dialog_note_event_title);
        titleText.setText("Add Note  (" + (position + 1) + "/" + events.size() + ")");

        TextView nameText = (TextView) view.findViewById(R.id.dialog_note_event_name);
        nameText.setText(behaviourName);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS", Locale.UK);
        TextView startText = (TextView) view.findViewById(R.id.dialog_note_event_start);
        startText.setText(sdf.format(event.getStartTime()));

        final EditText noteText = (EditText) view.findViewById(R.id.dialog_note_event_text);
        if (event.getNote().length() > 0) {
            noteText.setText(event.getNote());
        }

        if (position != 0) {
            dialog.setNegativeButton("Previous", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    event.setNote(noteText.getText().toString());
                    showNotesDialog(behaviours, events, position - 1);
                }
            });
        } else {
            if (position != events.size() - 1) {
                dialog.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        event.setNote(noteText.getText().toString());
                        saveSession();
                    }
                });
            }
        }

        if (position != events.size() - 1) {
            dialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    event.setNote(noteText.getText().toString());
                    showNotesDialog(behaviours, events, position + 1);
                }
            });
        } else {
            dialog.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    event.setNote(noteText.getText().toString());
                    saveSession();
                }
            });
        }

        dialog.show();
    }

    private void showStateDialog(final List<Behaviour> behaviours, final List<Integer[]> events, String behaviourName, final Event event, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_note_state, null);
        dialog.setView(view);

        TextView titleText = (TextView) view.findViewById(R.id.dialog_note_state_title);
        titleText.setText("Add Note  (" + (position + 1) + "/" + events.size() + ")");

        TextView nameText = (TextView) view.findViewById(R.id.dialog_note_state_name);
        nameText.setText(behaviourName);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS", Locale.UK);
        TextView startText = (TextView) view.findViewById(R.id.dialog_note_state_start);
        startText.setText(sdf.format(event.getStartTime()));

        TextView durationText = (TextView) view.findViewById(R.id.dialog_note_state_duration);
        durationText.setText(event.getDuration() + "s");

        final EditText noteText = (EditText) view.findViewById(R.id.dialog_note_state_text);
        if (event.getNote().length() > 0) {
            noteText.setText(event.getNote());
        }

        if (position != 0) {
            dialog.setNegativeButton("Previous", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    event.setNote(noteText.getText().toString());
                    showNotesDialog(behaviours, events, position - 1);
                }
            });
        } else {
            if (position != events.size() - 1) {
                dialog.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        event.setNote(noteText.getText().toString());
                        saveSession();
                    }
                });
            }
        }

        if (position != events.size() - 1) {
            dialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    event.setNote(noteText.getText().toString());
                    showNotesDialog(behaviours, events, position + 1);
                }
            });
        } else {
            dialog.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    event.setNote(noteText.getText().toString());
                    saveSession();
                }
            });
        }

        dialog.show();
    }

    private void saveSession() {
        boolean saved = FileHandler.saveSession(activeFolder, activeSession, filename, observation);
        if (saved) {
            makeSomeToast("File saved.");
            if (observation == activeSession.getObservationsCount()) {
                calculateStatistics();
                backToHome();
            } else {
                observation++;
                setupSession();
            }
        } else {
            makeSomeToast("Error when saving.");
        }
    }

    private void calculateStatistics() {
        // TODO: This isn't the nicest of places to do this. Should also be an option to generate these from session history screen.

        int nObservations = activeSession.getObservationsCount();
        float[][] durationStatistics = new float[nObservations][activeSession.getTemplate(1).behaviours.size()];
        int[][] frequencyStatistics = new int[nObservations][durationStatistics[0].length];
        boolean[][] marked = new boolean[nObservations][durationStatistics[0].length];
        String name = nObservations > 1 ? namePrefix : activeSession.getName();

        for (int observation = 0; observation < nObservations; observation++) {
            Template template = activeSession.getTemplate(observation + 1); // getTemplate is 1-indexed
            Behaviour[] behaviours = template.behaviours.toArray(new Behaviour[template.behaviours.size()]);

            for (int i = 0; i < behaviours.length; i++) {
                Behaviour behaviour = behaviours[i];
                marked[observation][i] = behaviour.isMarked();

                if (behaviour.getType() == BehaviourType.STATE) {
                    float avgDuration = 0.0f;
                    int count = behaviour.eventHistory.size();
                    for (Event event : behaviour.eventHistory) {
                        float duration = Float.parseFloat(event.duration);
                        avgDuration += duration;
                    }

                    if (count > 0) {
                        avgDuration = avgDuration / (float) count;
                    }

                    frequencyStatistics[observation][i] = count;
                    durationStatistics[observation][i] = avgDuration;
                } else {
                    frequencyStatistics[observation][i] = behaviour.eventHistory.size();
                    durationStatistics[observation][i] = -1f;
                }
            }
        }

        if (nObservations > 1) {
            FileHandler.saveMultipleStatistics(activeSession, activeFolder, name, frequencyStatistics, durationStatistics, marked);
        } else {
            FileHandler.saveSingleStatistics(activeSession, activeFolder, frequencyStatistics[0], durationStatistics[0]);
        }

        Log.e("Behave", "Statistics calculated.");
    }

    private void backToHome() {
        Intent intent = new Intent(SessionActivity.this, HomeActivity.class);
        intent.putExtra("activeFolderString", new File(FileHandler.getSessionsDirectory(), activeFolder).getAbsolutePath());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (sessionStarted) {
            endSession(null);
        } else {
            backToHome();
        }
    }

    private void makeSomeToast(final String message) {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.BehaveMonitor.DBHelper;
import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.HomeActivity;
import com.example.BehaveMonitor.Observation;
import com.example.BehaveMonitor.ObservationActivity;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Session;
import com.example.BehaveMonitor.SessionActivity;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionFragment extends Fragment {

    private View rootView;
    private DBHelper db;

    private String sessionName;
    private String sessionLocation;

    private File activeFolder;
    private String activeFolderName;

    private Observation activeObservation;
    private String activeObservationName;

	public SessionFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_session_create, container, false);

        rootView.findViewById(R.id.session_name).clearFocus();

        db = DBHelper.getInstance(getActivity());

        HomeActivity homeActivity = (HomeActivity) getActivity();
        activeFolderName = homeActivity.getFolderName();
        activeObservationName = homeActivity.getObservationName();

        setFolderSpinner();
        setObservationSpinner();
        setNewButtons();

        setCreateButton();

        return rootView;
	}

	private void setNewButtons() {
		ImageButton newFolderButton = (ImageButton) rootView.findViewById(R.id.new_folder);
		newFolderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newFolder(getActivity());
			}
		});

        ImageButton newObservationButton = (ImageButton) rootView.findViewById(R.id.new_observation);
        newObservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                db.setFolder(activeFolderName);
                newObservation(getActivity());
            }
        });
	}

	private void newFolder(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_folder, null);
        alert.setView(dialogView);
        final EditText input = (EditText) dialogView.findViewById(R.id.dialog_folder_name);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String folderName = input.getText().toString();
                folderName = folderName.trim();
                if (validateFolderName(folderName)) {
                    FileHandler.createNewFolder(folderName);
                    activeFolderName = folderName;
                    setFolderSpinner();
                }
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
		alert.show();
	}

    //Returns true if the foldername is valid
    private boolean validateFolderName(String foldername) {
        if (foldername.equals("")) return false;
        String foldernamePattern = "[a-zA-Z0-9_ ]+";
        Pattern pattern = Pattern.compile(foldernamePattern);
        Matcher matcher = pattern.matcher(foldername);
        return matcher.matches();
    }

    private void newObservation(final Context context) {
        Intent intent = new Intent(context, ObservationActivity.class);
        intent.putExtra("fromFragment", false);
        startActivity(intent);
    }

	private void setFolderSpinner() {
		String[] folders = FileHandler.getFolders();
        if (folders == null) {
            FileHandler.checkFoldersExist(getActivity());
            folders = FileHandler.getFolders();
        }

		Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_spinner);

		// Create an ArrayAdapter using the string array and a default spinner layout
		Log.w("spinner", spinner.toString());
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_item,
                folders);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

        if (!"".equals(activeFolderName)) {
            int i = 0;
            while (i < folders.length && i != -1) {
                if (folders[i].equals(activeFolderName)) {
                    spinner.setSelection(i);
                    i = -2;
                }

                i++;
            }
        }
	}

    private Spinner setObservationSpinner() {
        String[] observations = FileHandler.getObservationNames();
        if (observations == null || observations.length == 0) {
            observations = new String[]{"No observations exist."};
        } else {
            for (String s : observations) {
                Log.e("Behave", "Folder name: " + s);
            }
        }

        Spinner spinner = (Spinner) rootView.findViewById(R.id.observation_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, observations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (!"".equals(activeObservationName)) {
            int i = 0;
            while (i < observations.length && i != -1) {
                if (observations[i].equals(activeObservationName)) {
                    spinner.setSelection(i);
                    i = -2;
                }

                i++;
            }
        }

        return spinner;
    }

    private void setCreateButton() {
        Button button = (Button) rootView.findViewById(R.id.session_create_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFolder();
                if (validate()) {
                    if (loadSelectedObservation()) {
                        createSession();
                    }
                }
            }
        });
    }

    private boolean validate() {
        sessionName = ((EditText) rootView.findViewById(R.id.session_name)).getText().toString();
        sessionLocation = ((EditText) rootView.findViewById(R.id.session_location)).getText().toString();

        Spinner spinner = (Spinner) rootView.findViewById(R.id.observation_spinner);
        activeObservationName = spinner.getSelectedItem().toString();

        if ("No observations exist.".equals(activeObservationName)) {
            makeSomeToast("Must select an observation");
            return false;
        }

        if ("".equals(sessionName)) {
            makeSomeToast("Must enter a name");
            return false;
        }

        if ("".equals(sessionLocation)) {
            makeSomeToast("Must enter a location");
            return false;
        }

        if (!FileHandler.checkSessionName(activeFolderName, sessionName, sessionLocation)) {
            makeSomeToast("Session name already exists, will be saved with a version number");
        }

        return true;
    }

    private void setActiveFolder() {
        Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_spinner);

        String folderName = spinner.getSelectedItem().toString();
        activeFolder = new File(FileHandler.getSessionsDirectory(), folderName);
        activeFolderName = activeFolder.getName();
    }

    private boolean loadSelectedObservation() {
        Spinner spinner = (Spinner) rootView.findViewById(R.id.observation_spinner);
        String observationName = spinner.getSelectedItem().toString();

        String observation = FileHandler.readObservation(observationName);

        if (!"".equals(observation)) {
            activeObservation = new Observation(observation);
            activeObservationName = observationName;
            return true;
        } else {
            makeSomeToast("Error reading observation.");
        }

        return false;
    }

    /**
     * Creates new intent to go to SessionActivity, adds the user defined Session
     * (parcelable so can be putExtra-ed no hassle),
     * starts Activity.
     */
    public void createSession() {
        db.setFolderObservation(activeFolderName, activeObservation.toString());

        Session newSession = new Session(sessionName, sessionLocation, activeFolder.getAbsolutePath());
        newSession.setObservation(activeObservation);

        Intent intent = new Intent(getActivity(), SessionActivity.class);
        intent.putExtra("activeFolderString", activeFolderName);
        intent.putExtra("Session", newSession);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);
    }

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

//    /**
//     * Takes a path to a file and checks if the file is a observation.
//     * @param filePath the path to the file to check
//     * @return true if the filePath has the extension .observation
//     */
//    public boolean checkObservationExtension(String filePath) {
//        String[] parts = filePath.split("\\.");
//        if (parts.length == 0) return false;
//        return parts[parts.length - 1].equals(".observation");
//    }

//	public void setActiveFolderButton(final View rootView) {
//		Button button = (Button) rootView.findViewById(R.id.select_folder);
//		button.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_spinner);
//
//				String folderName = spinner.getSelectedItem().toString();
//				File activeFolder = new File(FileHandler.getSessionsDirectory(), folderName);
//				((HomeActivity) getActivity()).setActiveFolder(activeFolder);
//
//				// Change colour and move to next fragment
//				((HomeActivity) getActivity()).displayFragment(1);
//			}
//
//		});
//	}

}

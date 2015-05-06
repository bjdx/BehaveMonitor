//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.BehaveMonitor.DBHelper;
import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.adapters.SessionHistoryListAdapter;

import java.io.File;
import java.util.List;

public class SessionHistoryFragment extends Fragment {

    private View rootView;
    private SessionHistoryListAdapter sessionAdapter;
    private String activeFolder = "Default";

	public SessionHistoryFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_session_history, container,
				false);

        initSpinner();
        initList();

		return rootView;
	}

    private void initSpinner() {
        String[] folders = FileHandler.getFolders();
        if (folders == null) {
            FileHandler.checkFoldersExist(getActivity());
            folders = FileHandler.getFolders();
        }

        Spinner spinner = (Spinner) rootView.findViewById(R.id.history_folder_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> folderAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                folders);
        folderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(folderAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<File> folders = FileHandler.getSessions(((TextView) view).getText().toString());
//                Log.e("Behave", "" + folders.size());
                if (sessionAdapter != null) {
                    sessionAdapter.updateSessions(folders);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        // Get the most recently used folder from the database, Default if no folder found.
        DBHelper db = DBHelper.getInstance(getActivity());
        String folder = db.getFolder();
        if (folder != null) {
            activeFolder = folder;
        }

        int i = 0;
        while (i < folders.length && i != -1) {
            if (folders[i].equals(folder)) {
                spinner.setSelection(i);
                i = -2; // Jump out of loop.
            }

            i++;
        }
    }

    private void initList() {
        List<File> sessions = FileHandler.getSessions(activeFolder);

        ListView list = (ListView) rootView.findViewById(R.id.session_history_fragment_list);
        sessionAdapter = new SessionHistoryListAdapter(getActivity(), sessions);

        list.setAdapter(sessionAdapter);
    }
}

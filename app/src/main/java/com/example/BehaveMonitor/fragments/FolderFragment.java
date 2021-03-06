//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.adapters.FolderListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FolderFragment extends Fragment {

    private View rootView;
    private FolderListAdapter adapter;

    public FolderFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_folder, container, false);

        setList();
		setNewButton();
		return rootView;
	}

	private void setNewButton() {
		Button newFolderButton = (Button) rootView.findViewById(R.id.new_folder);
		newFolderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newFolder(getActivity());
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
                    adapter.addItem(folderName);
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

    private void setList() {
        List<String> folderNames = new ArrayList<>(Arrays.asList(FileHandler.getFolders()));
        List<Integer> sessionCounts = FileHandler.getSessionCounts();

        ListView list = (ListView) rootView.findViewById(R.id.folder_list);
        adapter = new FolderListAdapter(getActivity(), folderNames, sessionCounts);

        list.setAdapter(adapter);
    }
}

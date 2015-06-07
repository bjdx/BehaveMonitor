//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.BehaveMonitor.DBHelper;
import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.adapters.SessionHistoryListAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SessionHistoryFragment extends Fragment {

    private View rootView;
    private SessionHistoryListAdapter sessionAdapter;
    private String activeFolder = "Default";

	public SessionHistoryFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_session_history, container, false);

        initSpinner();
        initList();
        initZipButton();

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
                String folderName = ((TextView) view).getText().toString();
                List<File> folders = FileHandler.getSessions(folderName);
                if (sessionAdapter != null) {
                    activeFolder = folderName;
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
        sessionAdapter = new SessionHistoryListAdapter(getActivity(), sessions, (CheckBox) rootView.findViewById(R.id.select_all_check));

        list.setAdapter(sessionAdapter);
    }

    private void initZipButton() {
        Button button = (Button) rootView.findViewById(R.id.zip_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> files = sessionAdapter.getCheckedFiles();
                if (files.size() > 0) {
                    // Turn list of files into array of file paths.
                    String[] paths = new String[files.size()];
                    for (int i = 0; i < paths.length; i++) {
                        File file = files.get(i);
                        paths[i] = file.getAbsolutePath();
                    }

                    DBHelper db = DBHelper.getInstance(getActivity());
                    String email = db.getEmail();
                    if ("".equals(email)) {
                        showEmailDialog(paths);
                    } else {
                        compressAndEmail(paths, email);
                    }
                } else {
                    makeSomeToast("You must choose some files.");
                }
            }
        });
    }

    private void showEmailDialog(final String[] paths) {
        final Context context = getActivity();

        new MaterialDialog.Builder(context)
                .title("Set Email Address")
                .customView(R.layout.dialog_history_email, true)
                .negativeText("Cancel")
                .positiveText("Ok")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        if (view == null) {
                            return;
                        }

                        EditText input = (EditText) view.findViewById(R.id.dialog_history_email_address);
                        String email = input.getText().toString().trim();
                        if (!"".equals(email)) {
                            DBHelper db = DBHelper.getInstance(context);
                            db.setEmail(email);
                        }

                        compressAndEmail(paths, email);
                    }
                })
                .show();
    }

    private void compressAndEmail(String[] paths, String email) {
        String zipPath = FileHandler.getPath(activeFolder) + File.separator + activeFolder + "_files.zip";

        try {
            BufferedInputStream input;
            FileOutputStream dest = new FileOutputStream(zipPath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte[] data = new byte[2048];

            for (String path : paths) {
                Log.e("Behave", "Compressing file: " + path);
                FileInputStream fi = new FileInputStream(path);
                input = new BufferedInputStream(fi, 2048);

                ZipEntry entry = new ZipEntry(path.substring(path.lastIndexOf(File.separator) + 1)); // Get the file name from the path.
                out.putNextEntry(entry);

                int count;
                while ((count = input.read(data, 0, 2048)) != -1) {
                    out.write(data, 0, count);
                }

                input.close();
            }

            out.close();

            File zipFile = new File(zipPath);
            FileHandler.sendEmail(getActivity(), email, zipFile);

        } catch (FileNotFoundException e) {
            makeSomeToast("Couldn't find file");
            Log.e("Behave", "FNF exception");
        } catch (IOException e) {
            makeSomeToast("Other error");
            Log.e("Behave", "IO exception");
        }
    }

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

package com.example.BehaveMonitor;

import java.io.File;

import com.example.BehaveMonitor.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class FolderFragment extends Fragment {

    private String activeFolder;

	public FolderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_folder, container,
				false);

        MainActivity mA = (MainActivity) getActivity();
        activeFolder = mA.getFolderName();

		setDeleteButton(rootView);
		setSpinner(rootView);
		setNewButton(rootView);
		setActiveFolderButton(rootView);
		return rootView;
	}

	public void setNewButton(View rootView) {
		ImageButton button = (ImageButton) rootView
				.findViewById(R.id.new_folder);
		Log.w("button", button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				newFolder(getActivity());
			}

		});
	}

	public void newFolder(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Create New Folder");
		alert.setMessage("New folder name:");

		// Set an EditText view to get user input
		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String folderName = input.getText().toString();
				String dirPath = context.getFilesDir().getAbsolutePath()
						+ File.separator + "Session Folders" + File.separator
						+ folderName;
				File projDir = new File(dirPath);
				if (!projDir.exists()) {
					try {
						projDir.mkdirs();
						setSpinner(getView());
					} catch (Exception e) {
						Log.e("ERROR", "Failed to create directory!");
					}
				} else {
					// The folder already exists
					Log.e("ERROR", "Directory already exists!");
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	public void setDeleteButton(final View rootView) {
		ImageButton button = (ImageButton) rootView
				.findViewById(R.id.delete_folder);
		Log.w("button", button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				deleteFolder(getActivity(), rootView);
			}

		});
	}

	public void deleteFolder(final Context context, final View rootView) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Delete Current Folder");
		alert.setMessage("Are you sure you want to delete this folder? (Data inside the folder will be lost!)");

		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Spinner spinner = (Spinner) rootView
						.findViewById(R.id.folder_spinner);
				String folderName = spinner.getSelectedItem().toString();
				String dirPath = context.getFilesDir().getAbsolutePath()
						+ File.separator + "Session Folders" + File.separator
						+ folderName;
				File projDir = new File(dirPath);
				if (projDir.exists()) {
					try {
						projDir.delete();
						setSpinner(getView());
					} catch (Exception e) {
						Log.e("ERROR", "Failed to delete directory!");
					}
				} else {
					// The folder already exists
					Log.e("ERROR", "Directory doesn't exist!");
				}
			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	public void setSpinner(View rootView) {

		String[] folders = getFolders();

		Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_spinner);

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		Log.w("spinner", spinner.toString());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, folders);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

        if(activeFolder!=null) {
            int i = 0;
            for(String s:folders) {
                if(s.equals(activeFolder)) spinner.setSelection(i);
                else i++;
            }
        }

	}

	public String[] getFolders() {
		String dirPath = getActivity().getFilesDir().getAbsolutePath()
				+ File.separator + "Session Folders";
		File projDir = new File(dirPath);
		String[] folders = projDir.list();
		if (folders.length == 0)
			Log.e("getFolders Error", "The Session Folder doesn't exist.");

		return projDir.list();
	}

	public void setActiveFolderButton(final View rootView) {
		Button button = (Button) rootView.findViewById(R.id.select_folder);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Spinner spinner = (Spinner) rootView
						.findViewById(R.id.folder_spinner);
				String folderName = spinner.getSelectedItem().toString();
				String dirPath = getActivity().getFilesDir().getAbsolutePath()
						+ File.separator + "Session Folders" + File.separator
						+ folderName;
				File activeFolder = new File(dirPath);
				((MainActivity) getActivity()).setActiveDir(activeFolder);
				// Change colour and move to next fragment

				spinner.setBackgroundColor(Color.parseColor("#33B5E5"));
				((MainActivity) getActivity()).selectItem(1);
			}

		});
	}

}

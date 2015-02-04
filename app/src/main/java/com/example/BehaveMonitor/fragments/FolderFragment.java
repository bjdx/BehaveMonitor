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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.adapters.FolderListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FolderFragment extends Fragment {

    private View rootView;
//    private String activeFolder;

	public FolderFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_folder, container,
				false);

//        HomeActivity mA = (HomeActivity) getActivity();
//        activeFolder = mA.getFolderName();

//		setDeleteButton();
//		setSpinner();
        setList();
		setNewButton();
//		setActiveFolderButton();
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
                if (!"".equals(folderName)) {
                    FileHandler.createNewFolder(folderName);
//                    activeFolder = folderName;
//                    setSpinner();
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

    private void setList() {
        List<String> folderNames = new ArrayList<>(Arrays.asList(FileHandler.getFolders()));
        List<Integer> sessionCounts = FileHandler.getSessionCounts();

        ListView list = (ListView) rootView.findViewById(R.id.folder_list);
        FolderListAdapter adapter = new FolderListAdapter(getActivity(), folderNames, sessionCounts);

        list.setAdapter(adapter);
    }

	public void setDeleteButton() {
		ImageButton button = (ImageButton) rootView.findViewById(R.id.delete_folder);
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
				Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_spinner);
				String folderName = spinner.getSelectedItem().toString();
				FileHandler.deleteFolder(folderName);
//                activeFolder = "";
//                setSpinner();
			}
		});

		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

//	private void setSpinner() {
//		String[] folders = FileHandler.getFolders();
//		Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_spinner);
//
//		// Create an ArrayAdapter using the string array and a default spinner layout
//		Log.w("spinner", spinner.toString());
//		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
//				android.R.layout.simple_spinner_item,
//                folders);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner.setAdapter(adapter);
//
//        if (!"".equals(activeFolder)) {
//            int i = 0;
//            while (i < folders.length && i != -1) {
//                if (folders[i].equals(activeFolder)) {
//                    spinner.setSelection(i);
//                    i = -2;
//                }
//                i++;
//            }
//        }
//	}

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

//    /**
//     * Takes a path to a file and checks if the file is a template.
//     * @param filePath the path to the file to check
//     * @return true if the filePath has the extension .template
//     */
//    public boolean checkTemplateExtension(String filePath) {
//        String[] parts = filePath.split("\\.");
//        if (parts.length == 0) return false;
//        return parts[parts.length - 1].equals(".template");
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

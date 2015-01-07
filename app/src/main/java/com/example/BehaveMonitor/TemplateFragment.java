package com.example.BehaveMonitor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.BehaveMonitor.R;

public class TemplateFragment extends Fragment {

    private String activeTemplate;

	public TemplateFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_template, container, false);

		
		setSpinner(rootView);
		setNewButton(rootView);
		setDeleteButton(rootView);
		setSelectButton(rootView);
		return rootView;
	}
	
	public void setSelectButton(View rootView) {
		Button button = (Button) rootView.findViewById(R.id.select_template);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadSelectedTemplate();
			}
		
	
		});
		
		
	}
	
	public void loadSelectedTemplate() {
		Spinner spinner = (Spinner) getView().findViewById(R.id.template_spinner);
		String folderName = spinner.getSelectedItem().toString();
		FileInputStream fis;
		final StringBuffer storedString = new StringBuffer();
		String dirPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + "Templates" + File.separator;
		File file = new File(dirPath + folderName+".tmp");
		try {
		    Reader dataIO = new FileReader(file);
		    String strLine = null;
		    BufferedReader br = new BufferedReader(dataIO);
		    if ((strLine = br.readLine()) != null) {
		        storedString.append(strLine);
		    }
		    dataIO.close();
		}
		catch  (Exception e) {  
		}
		
		((MainActivity) getActivity()).setActiveTmp(new Template(storedString.toString()));
		((MainActivity) getActivity()).selectItem(2);
	}
	
	
	
	public void setSpinner(View rootView) {

		String[] folders = getTemplateNames();
		if(folders == null) {
			folders = new String[]{"No templates exist."};
		} else {
            for(String s: folders) {
                Log.d("Folder name:",s);
            }
        }

		Spinner spinner = (Spinner) rootView.findViewById(R.id.template_spinner);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		Log.w("spinner",spinner.toString());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, folders);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

        if(activeTemplate!=null) {
            int i = 0;
            for(String s:folders) {
                if(s.equals(activeTemplate)) spinner.setSelection(i);
                else i++;
            }
        }
	}
	
	public String[] getTemplateNames() {
		String dirPath = getActivity().getFilesDir().getAbsolutePath()+ File.separator + "Templates";
		File projDir = new File(dirPath);
		String[] files = projDir.list();
		if(files.length == 0) {
			Log.e("getFolders Error", "No templates exist.");
			return null;
		} else {
			
			String[] tmpNames = new String[files.length];
			Log.w("num of names",""+tmpNames.length);
			for (int i = 0; i < files.length; i++) {
				String[] parts = files[i].split("\\.");
				if(parts.length>1) {
					if(parts[1].equals("tmp")){
						tmpNames[i] = parts[0];
					}
				}
				
			}
			return tmpNames;
		}
	}
	

	
	public void setNewButton(View rootView) {
		ImageButton button = (ImageButton) rootView.findViewById(R.id.new_folder);
		Log.w("button",button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				newTemplate(getActivity());
			}
			
		
		});
	}
	
	
	
	
	public void newTemplate(final Context context) {
		Intent intent = new Intent(context, TemplateActivity.class);
        startActivity(intent);
	}

	public void setDeleteButton(final View rootView) {
		ImageButton button = (ImageButton) rootView.findViewById(R.id.delete_folder);
		Log.w("button",button.toString());
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Spinner spinner = (Spinner) rootView.findViewById(R.id.template_spinner);
				String tmpName = spinner.getSelectedItem().toString();
				if (tmpName.equals("No templates exist.")) {
					return;
				} else {
					deleteTemplate(getActivity(), rootView);
				}
			}
			
		
		});
	}
	
	public void deleteTemplate(final Context context, final View rootView) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
	
		alert.setTitle("Delete Current Template");
		alert.setMessage("Are you sure you want to delete this folder? (Template will not be reusable!)");
	
	
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Spinner spinner = (Spinner) rootView.findViewById(R.id.template_spinner);
			String tmpName = spinner.getSelectedItem().toString();

			Log.d("Files spinner", "Name:"+tmpName);
		  	String dirPath = context.getFilesDir().getAbsolutePath() + File.separator + "Templates" + File.separator + tmpName + ".tmp";
			File projDir = new File(dirPath);
			if (projDir.exists()) {
				try {
					projDir.delete();
					setSpinner(getView());
				} catch(Exception e) {
					Log.e("ERROR", "Failed to delete directory!");
				}
			} else {
				//The folder already exists
				Log.e("ERROR", "Directory doesn't exist!");
			}
		  }
		});
	
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Cancelled.
		  }
		});
	
		alert.show();
	}
	
	
	//returns true if the filePath has the extension .tmp
	public boolean checkTmpExt(String filePath) {
		String[] parts = filePath.split(".");
		return (parts[parts.length-1].equals(".tmp"));
	}

}

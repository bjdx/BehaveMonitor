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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.HomeActivity;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Template;
import com.example.BehaveMonitor.TemplateActivity;

import java.io.File;

public class TemplateFragment extends Fragment {

    private String activeTemplate;

	public TemplateFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_template, container, false);

        activeTemplate = ((HomeActivity) getActivity()).getTemplateName();
        if(!"".equals(activeTemplate)) {
            Spinner spinner = setSpinner(rootView);
//            Spinner spinner = (Spinner) rootView.findViewById(R.id.template_spinner);

            for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equals(activeTemplate)) {
                    spinner.setSelection(i, true);
                }
            }
        } else {
            setSpinner(rootView);
        }

		setNewButton(rootView);
		setDeleteButton(rootView);
		setSelectButton(rootView);

		return rootView;
	}

    @Override
    public void onResume() {
        super.onResume();

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
		String templateName = spinner.getSelectedItem().toString();

        String template = FileHandler.readTemplate(templateName);

        if (!"".equals(template)) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.setActiveTmp(new Template(template));
            homeActivity.displayFragment(2);
        } else {
            makeSomeToast("Error reading template.");
        }
	}
	
	public Spinner setSpinner(View rootView) {

		String[] folders = getTemplateNames();
		if(folders == null || folders.length == 0) {
			folders = new String[]{"No templates exist."};
		} else {
            for(String s: folders) {
                Log.d("Folder name:", s);
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

        if (activeTemplate != null) {
            int i = 0;
            for (String s : folders) {
                if (s.equals(activeTemplate)) spinner.setSelection(i);
                else i++;
            }
        }

        return spinner;
	}
	
	public String[] getTemplateNames() {
//		String dirPath = getActivity().getFilesDir().getAbsolutePath()+ File.separator + "Templates";
		File projDir = FileHandler.getTemplateDirectory();
		String[] files = projDir.list();
		if(files.length == 0) {
			Log.e("getFolders Error", "No templates exist.");
			return null;
		} else {
			
			String[] tmpNames = new String[files.length];
			Log.w("Behave", "Num of templates found: " + tmpNames.length);
			for (int i = 0; i < files.length; i++) {
				String[] parts = files[i].split("\\.");
				if (parts.length > 1) {
					if (parts[1].equals("template")) {
						tmpNames[i] = parts[0];
					}
				}
				
			}
			return tmpNames;
		}
	}
	
	public void setNewButton(View rootView) {
		ImageButton button = (ImageButton) rootView.findViewById(R.id.new_template);
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
					// Do nothing.
				} else {
					deleteTemplate(getActivity(), rootView);
				}
			}
		});
	}
	
	public void deleteTemplate(final Context context, final View rootView) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
	
		alert.setTitle("Delete Current Template");
		alert.setMessage("Are you sure you want to delete this template? (Template will not be reusable!)");
	
	
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			Spinner spinner = (Spinner) rootView.findViewById(R.id.template_spinner);
			String tmpName = spinner.getSelectedItem().toString();

            FileHandler.deleteTemplate(tmpName);
            setSpinner(getView());
		  }
		});
	
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Cancelled.
		  }
		});
	
		alert.show();
	}


    /**
     * Takes a path to a file and checks if the file is a template.
     * @param filePath the path to the file to check
     * @return true if the filePath has the extension .template
     */
	public boolean checkTemplateExtension(String filePath) {
		String[] parts = filePath.split(".");
		return (parts[parts.length-1].equals(".template"));
	}

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

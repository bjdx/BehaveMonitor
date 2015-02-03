package com.example.BehaveMonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SessionFragment extends Fragment {

	public SessionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_session, container, false);
        setCreateSession(rootView);
		return rootView;
	}

    //Adds listener to Create Session button to move to next Activity
    public void setCreateSession(View rootView) {
        Button button = (Button) rootView.findViewById(R.id.create_session);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createSession();
            }


        });
    }


    //Creates new intent to go to SessionActivity, adds the user defined Session
    //(parcelable so can be putExtra-ed no hassle)
    //starts Activity.
    public void createSession() {
        View rootView = getView();
        if (rootView == null) {
            return;
        }

        EditText nameET = (EditText) rootView.findViewById(R.id.session_name);
        EditText locET = (EditText) rootView.findViewById(R.id.session_location);
        String name = nameET.getText().toString();
        String location = locET.getText().toString();

        if (name.length() > 0 && location.length() > 0) {
            HomeActivity mA = (HomeActivity) getActivity();

            if(mA.getTemplateName().equals("")) {
                makeSomeToast("Please select a template to use in this session.");
                mA.displayFragment(1);
            }

            String activeFolder = mA.getFolderName();
            if ("".equals(activeFolder)) {
                makeSomeToast("Please select a folder to use in this session.");
                mA.displayFragment(0);

                return;
            } else {
                if (!FileHandler.checkSessionName(activeFolder, name)) {
                    makeSomeToast("A session of this name already exists in the selected folder.");
                    return;
                }
            }

            Session newSession = mA.makeSession(name, location);
            Intent intent = new Intent(getActivity(), SessionActivity.class);
            intent.putExtra("activeFolderString", mA.getFolderName());
            intent.putExtra("Session", newSession);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(intent);

        } else {
            makeSomeToast("Please enter a name and location for this session before continuing.");
        }
    }

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final CharSequence text = message;
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}

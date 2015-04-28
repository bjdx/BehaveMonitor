//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.ObservationActivity;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.adapters.ObservationListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObservationFragment extends Fragment {

    private View rootView;

	public ObservationFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_observation, container, false);

        setList();
		setNewButton();
		return rootView;
	}

    private void setList() {
        List<String> observationNames = new ArrayList<>(Arrays.asList(FileHandler.getObservationNames()));

        ListView list = (ListView) rootView.findViewById(R.id.observation_list);
        ObservationListAdapter adapter = new ObservationListAdapter(getActivity(), observationNames);

        list.setAdapter(adapter);
    }

	private void setNewButton() {
		Button button = (Button) rootView.findViewById(R.id.new_observation);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newObservation(getActivity());
			}
		});
	}
	
	public void newObservation(final Context context) {
		Intent intent = new Intent(context, ObservationActivity.class);
        intent.putExtra("fromFragment", true);
        startActivity(intent);
	}
}

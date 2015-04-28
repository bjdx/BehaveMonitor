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
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.TemplateActivity;
import com.example.BehaveMonitor.adapters.TemplateListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateFragment extends Fragment {

    private View rootView;

	public TemplateFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_template, container, false);

        setList();
		setNewButton();
		return rootView;
	}

    private void setList() {
        List<String> templateNames = new ArrayList<>(Arrays.asList(FileHandler.getTemplateNames()));

        ListView list = (ListView) rootView.findViewById(R.id.template_list);
        TemplateListAdapter adapter = new TemplateListAdapter(getActivity(), templateNames);

        list.setAdapter(adapter);
    }

	private void setNewButton() {
		Button button = (Button) rootView.findViewById(R.id.new_template);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newTemplate(getActivity());
			}
		});
	}
	
	public void newTemplate(final Context context) {
		Intent intent = new Intent(context, TemplateActivity.class);
        intent.putExtra("fromFragment", true);
        startActivity(intent);
	}
}

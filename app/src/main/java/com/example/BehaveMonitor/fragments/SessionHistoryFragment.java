package com.example.BehaveMonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.adapters.SessionHistoryListAdapter;

import java.io.File;
import java.util.List;

public class SessionHistoryFragment extends Fragment {

    private View rootView;

	public SessionHistoryFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_session_history, container,
				false);

        setList();
		return rootView;
	}

    private void setList() {
        List<File> sessions = FileHandler.getSessions();

        ListView list = (ListView) rootView;
        SessionHistoryListAdapter adapter = new SessionHistoryListAdapter(getActivity(), sessions);

        list.setAdapter(adapter);
    }
}

//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Observation;
import com.example.BehaveMonitor.ObservationActivity;

import java.util.List;

public class ObservationListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> observations;

    public ObservationListAdapter(Context context, List<String> observations) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.observations = observations;
    }

    private class ViewHolder {
        TextView observationName;
        ImageButton deleteButton, editButton;
    }

    @Override
    public int getCount() {
        return observations.size();
    }

    @Override
    public Object getItem(int position) {
        return observations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.observation_list_item, parent, false);

            viewHolder.observationName = (TextView) convertView.findViewById(R.id.list_observation_name);
            viewHolder.editButton = (ImageButton) convertView.findViewById(R.id.list_observation_edit_button);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.list_observation_delete_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String observation = observations.get(position);
        viewHolder.observationName.setText(observation);
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editObservation(position);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteObservation(position);
            }
        });

        return convertView;
    }

    private void editObservation(int position) {
        String observationName = observations.get(position);
        Observation observation = new Observation(FileHandler.readObservation(observationName));

        Intent intent = new Intent(context, ObservationActivity.class);
        intent.putExtra("observation", observation);
        intent.putExtra("fromFragment", true);

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    private void deleteObservation(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final String observationName = observations.get(position);
        alert.setTitle("Delete " + observationName + "?");
        alert.setMessage("Are you sure you want to delete this observation? (Observation will be lost forever!)");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FileHandler.deleteObservation(observationName);
                observations.remove(position);
                notifyDataSetChanged();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}

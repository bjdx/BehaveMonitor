//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.BehaveMonitor.DBHelper;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Setting;
import com.example.BehaveMonitor.SettingsItem;
import com.example.BehaveMonitor.adapters.SettingsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private View rootView;

    public SettingsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initList();

        return rootView;
    }

    private void initList() {
        List<SettingsItem> items = new ArrayList<>();
        createSettingsItems(items);

        final SettingsListAdapter adapter = new SettingsListAdapter(getActivity(), items);
        ListView list = (ListView) rootView;
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openSetting(adapter.getSetting(position));
            }
        });
    }

    private void openSetting(Setting setting) {
        switch (setting) {
            case EMAIL:
                showEmailDialog();
                break;
            case DEFAULT_OBSERVATIONS:
                showDefaultObservationsDialog();
                break;
            case MAX_OBSERVATIONS:
                showMaxObservationsDialog();
                break;
            default:
                break;
        }
    }

    private void showEmailDialog() {
        final Context context = getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_settings_email, null);

        DBHelper db = DBHelper.getInstance(context);
        String email = db.getEmail();

        final EditText input = (EditText) view.findViewById(R.id.dialog_settings_email_address);
        input.setText(email);
        dialog.setView(view);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();

                DBHelper db = DBHelper.getInstance(context);
                db.setEmail(email);

                makeSomeToast("Email changed.");
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    private void showDefaultObservationsDialog() {
        final Context context = getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_settings_default_observations, null);

        DBHelper db = DBHelper.getInstance(context);
        int amount = db.getDefaultObservationsAmount();

        final EditText input = (EditText) view.findViewById(R.id.dialog_settings_default_observations_amount);
        input.setText("" + amount);
        dialog.setView(view);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int newAmount = Integer.parseInt(input.getText().toString().trim());

                    DBHelper db = DBHelper.getInstance(context);
                    int max = db.getMaxObservationsAmount();

                    db.setDefaultObservationsAmount(newAmount);
                    if (newAmount > max) {
                        db.setMaxObservationsAmount(newAmount);
                        makeSomeToast("Default and maximum set to " + newAmount);
                    } else {
                        makeSomeToast("Default set to " + newAmount);
                    }
                } catch (NumberFormatException e) {
                    makeSomeToast("Must enter a number.");
                }
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    private void showMaxObservationsDialog() {
        final Context context = getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_settings_max_observations, null);

        DBHelper db = DBHelper.getInstance(context);
        int amount = db.getMaxObservationsAmount();

        final EditText input = (EditText) view.findViewById(R.id.dialog_settings_max_observations_amount);
        input.setText("" + amount);
        dialog.setView(view);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int newAmount = Integer.parseInt(input.getText().toString().trim());

                    DBHelper db = DBHelper.getInstance(context);
                    int currentDefault = db.getDefaultObservationsAmount();

                    db.setMaxObservationsAmount(newAmount);
                    if (newAmount < currentDefault) {
                        db.setDefaultObservationsAmount(newAmount);
                        makeSomeToast("Default and maximum set to " + newAmount);
                    } else {
                        makeSomeToast("Max set to " + newAmount);
                    }
                } catch (NumberFormatException e) {
                    makeSomeToast("Must enter a number.");
                }
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    private void createSettingsItems(List<SettingsItem> items) {
        SettingsItem email = new SettingsItem();
        email.setSetting(Setting.EMAIL);
        email.setHeading("Default email");
        email.setSubheading("Change the default recipient email address");
        items.add(email);

        SettingsItem defaultObservation = new SettingsItem();
        defaultObservation.setSetting(Setting.DEFAULT_OBSERVATIONS);
        defaultObservation.setHeading("Default observations amount");
        defaultObservation.setSubheading("Change the default number of observations in a session");
        items.add(defaultObservation);

        SettingsItem maxObservations = new SettingsItem();
        maxObservations.setSetting(Setting.MAX_OBSERVATIONS);
        maxObservations.setHeading("Maximum observations");
        maxObservations.setSubheading("Change the maximum number of observations in a session");
        items.add(maxObservations);
    }

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

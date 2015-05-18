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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.BehaveMonitor.DBHelper;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Setting;
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
        List<Setting> items = new ArrayList<>();
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

    private void openSetting(int setting) {
        switch (setting) {
            case Setting.EMAIL:
                showEmailDialog();
                break;
            case Setting.DEFAULT_OBSERVATIONS:
                showDefaultObservationsDialog();
                break;
            case Setting.MAX_OBSERVATIONS:
                showMaxObservationsDialog();
                break;
            case Setting.SHOW_RENAME_DIALOG:
                showRenameDialog();
                break;
            case Setting.NAME_PREFIX:
                showNamePrefixDialog();
                break;
            case Setting.DECIMAL_PLACES:
                showDecimalPlacesDialog();
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

    private void showRenameDialog() {
        final Context context = getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_settings_rename_dialog, null);
        dialog.setView(view);

        final DBHelper db = DBHelper.getInstance(context);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_settings_rename_dialog_check);
        checkBox.setChecked(db.getShowRenameDialog());

        dialog.setNegativeButton("Cancel", null);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.setShowRenameDialog(checkBox.isChecked());
            }
        });

        dialog.show();
    }

    private void showNamePrefixDialog() {
        final Context context = getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_settings_name_prefix, null);
        dialog.setView(view);

        final RadioButton prefixRadio = (RadioButton) view.findViewById(R.id.dialog_settings_name_prefix_option);
        final RadioButton dialogRadio = (RadioButton) view.findViewById(R.id.dialog_settings_name_dialog_option);

        final DBHelper db = DBHelper.getInstance(context);
        boolean prefix = db.getNamePrefix();
        if (prefix) {
            prefixRadio.setChecked(true);
        } else {
            dialogRadio.setChecked(true);
        }

        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.setNamePrefix(prefixRadio.isChecked());
            }
        });

        dialog.show();
    }

    private void showDecimalPlacesDialog() {
        final Context context = getActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_settings_decimal_places, null);
        dialog.setView(view);

        final EditText editText = (EditText) view.findViewById(R.id.dialog_settings_decimal_places_amount);
        final DBHelper db = DBHelper.getInstance(context);
        editText.setText("" + db.getDecimalPlaces());

        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int decimals = Integer.parseInt(editText.getText().toString());
                    db.setDecimalPlaces(decimals);
                    makeSomeToast("Decimal places updated");
                } catch (NumberFormatException e) {
                    makeSomeToast("Error: must enter a number");
                }
            }
        });

        dialog.show();
    }

    private void createSettingsItems(List<Setting> items) {
        Setting email = new Setting();
        email.setSetting(Setting.EMAIL);
        email.setHeading("Default email");
        email.setSubheading("Change the default recipient email address");
        items.add(email);

        Setting defaultObservation = new Setting();
        defaultObservation.setSetting(Setting.DEFAULT_OBSERVATIONS);
        defaultObservation.setHeading("Default observations amount");
        defaultObservation.setSubheading("Change the default number of observations in a session");
        items.add(defaultObservation);

        Setting maxObservations = new Setting();
        maxObservations.setSetting(Setting.MAX_OBSERVATIONS);
        maxObservations.setHeading("Maximum observations");
        maxObservations.setSubheading("Change the maximum number of observations in a session");
        items.add(maxObservations);

        Setting renameDialog = new Setting();
        renameDialog.setSetting(Setting.SHOW_RENAME_DIALOG);
        renameDialog.setHeading("Show rename dialog");
        renameDialog.setSubheading("Show/Hide option to rename observation before saving");
        items.add(renameDialog);

        Setting namePrefix = new Setting();
        namePrefix.setSetting(Setting.NAME_PREFIX);
        namePrefix.setHeading("Use name prefixing");
        namePrefix.setSubheading("Decide whether to use a name prefix scheme for multiple observations or ask for a name each time");
        items.add(namePrefix);

        Setting decimalPlaces = new Setting();
        decimalPlaces.setSetting(Setting.DECIMAL_PLACES);
        decimalPlaces.setHeading("Decimal places");
        decimalPlaces.setSubheading("Set how many decimal places to keep when calculating statistics");
        items.add(decimalPlaces);
    }

    private void makeSomeToast(final String message) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.BehaveMonitor.DBHelper;
import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SessionHistoryListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<File> sessions;
    private List<Boolean> checks;

    private CheckBox selectAllNone;
    private boolean checked = false;
    private int checkCount = 0;

    public SessionHistoryListAdapter(Context context, List<File> sessions, final CheckBox selectAllNone) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sessions = sessions;

        this.selectAllNone = selectAllNone;
        this.selectAllNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = !checked;

                if (checked) {
                    selectAll();
                } else {
                    clearAll();
                }
            }
        });

        this.checks = new ArrayList<>();
        for (int i = 0 ; i < sessions.size(); i++) {
            checks.add(false);
        }

        if (this.sessions.size() == 0) {
            this.sessions.add(null);
        }
    }

    public void updateSessions(List<File> sessions) {
        this.sessions = sessions;

        checked = false;
        checkCount = 0;

        if (selectAllNone.isChecked()) {
            selectAllNone.setChecked(false);
        }

        this.checks = new ArrayList<>();
        for (int i = 0 ; i < sessions.size(); i++) {
            checks.add(false);
        }

        if (this.sessions.size() == 0) {
            this.sessions.add(null);
        }

        notifyDataSetChanged();
    }

    public List<File> getCheckedFiles() {
        List<File> files = new ArrayList<>();

        if (checks.size() != sessions.size()) {
            Log.e("Behave", "Sessions and checks different sizes");
        }

        for (int i = 0; i < sessions.size(); i++){
            if (checks.size() > i) {
                if (checks.get(i)) {
                    files.add(sessions.get(i));
                }
            }
        }

        return files;
    }

    public void selectAll() {
        for (int i = 0; i < checks.size(); i++) {
            checks.set(i, true);
        }

        checkCount = sessions.size();
        notifyDataSetChanged();
    }

    public void clearAll() {
        for (int i = 0; i < checks.size(); i++) {
            checks.set(i, false);
        }

        checkCount = 0;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox checkBox;
        TextView sessionName;
        ImageButton emailButton, deleteButton;
//        Button actionsButton;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int position) {
        return sessions.get(position);
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
    public int getItemViewType(int position) {
        return sessions.get(position) == null ? 0 : 1; // 0 for empty row, 1 otherwise.
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int type = getItemViewType(position);

        Log.e("Behave", "Position: " + position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            if (type == 0) {
                convertView = inflater.inflate(R.layout.session_history_empty_row, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.session_history_list_item, parent, false);

                viewHolder.sessionName = (TextView) convertView.findViewById(R.id.list_session_name);
                viewHolder.emailButton = (ImageButton) convertView.findViewById(R.id.list_sessions_email_btn);
                viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.list_sessions_delete_btn);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.list_session_check);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (type == 0) {
            return convertView;
        }

        File session = sessions.get(position);
        viewHolder.sessionName.setText(getName(session));
        viewHolder.checkBox.setChecked(checks.get(position));

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checks.set(position, !checks.get(position));
                if (checks.get(position)) {
                    checkCount++;
                    if (checkCount == sessions.size()) {
                        selectAllNone.setChecked(true);
                        checked = true;
                    }
                } else {
                    checkCount--;
                    if (checkCount == 0) {
                        selectAllNone.setChecked(false);
                        checked = false;
                    }
                }
            }
        });

        viewHolder.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = DBHelper.getInstance(context);
                String email = dbHelper.getEmail();
                if ("".equals(email)) {
                    showEmailDialog(position);
                } else {
                    FileHandler.sendEmail(context, email, sessions.get(position));
                }
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSession(position);
            }
        });

        return convertView;
    }

    private String getName(File file) {
        return file.getName().split("\\.(?=[^\\.]+$)")[0];
    }

    private void deleteSession(final int position) {
        final File session = sessions.get(position);

        new MaterialDialog.Builder(context)
                .title("Delete " + getName(session) + "?")
                .content("Are you sure you want to delete this session?")
                .negativeText("Cancel")
                .positiveText("Yes")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        FileHandler.deleteSession(session);
                        sessions.remove(position);

                        if (sessions.size() == 0) {
                            sessions.add(null);
                        }

                        notifyDataSetChanged();
                    }
                })
                .show();
    }

    private void showEmailDialog(final int position) {
        new MaterialDialog.Builder(context)
                .title("Set Email Address")
                .customView(R.layout.dialog_history_email, true)
                .negativeText("Cancel")
                .positiveText("Ok")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        if (view == null) {
                            return;
                        }

                        EditText input = (EditText) view.findViewById(R.id.dialog_history_email_address);
                        String email = input.getText().toString().trim();
                        if (!"".equals(email)) {
                            DBHelper db = DBHelper.getInstance(context);
                            db.setEmail(email);
                        }

                        FileHandler.sendEmail(context, email, sessions.get(position));
//                        compressAndEmail(paths, email);
                    }
                })
                .show();
    }
}

//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

        notifyDataSetChanged();
    }

    public List<File> getCheckedFiles() {
        List<File> files = new ArrayList<>();

        for (int i = 0; i < sessions.size(); i++){
            if (checks.get(i)) {
                files.add(sessions.get(i));
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.session_history_list_item, parent, false);

            viewHolder.sessionName = (TextView) convertView.findViewById(R.id.list_session_name);
            viewHolder.emailButton = (ImageButton) convertView.findViewById(R.id.list_sessions_email_btn);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.list_sessions_delete_btn);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.list_session_check);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
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
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final File session = sessions.get(position);
        alert.setTitle("Delete " + getName(session) + "?");
        alert.setMessage("Are you sure you want to delete this session?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FileHandler.deleteSession(session);
                sessions.remove(position);

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

    private void showEmailDialog(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        View view = View.inflate(context, R.layout.dialog_history_email, null);
        final EditText input = (EditText) view.findViewById(R.id.dialog_history_email_address);
        dialog.setView(view);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();

                if (!"".equals(email)) {
                    DBHelper db = DBHelper.getInstance(context);
                    db.setEmail(email);
                }

                FileHandler.sendEmail(context, email, sessions.get(position));
            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }
}

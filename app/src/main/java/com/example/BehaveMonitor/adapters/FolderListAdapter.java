//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;

import java.util.List;

public class FolderListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> folders;
    private List<Integer> sessionCounts;

    public FolderListAdapter(Context context, List<String> folders, List<Integer> sessionCounts) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.folders = folders;
        this.sessionCounts = sessionCounts;
    }

    public void addItem(String folderName) {
        folders.add(folderName);
        sessionCounts.add(0);
    }

    private class ViewHolder {
        TextView folderName, numberSessions;
        ImageButton deleteButton;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Object getItem(int position) {
        return folders.get(position);
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

            convertView = inflater.inflate(R.layout.folder_list_item, parent, false);

            viewHolder.folderName = (TextView) convertView.findViewById(R.id.list_folder_name);
            viewHolder.numberSessions = (TextView) convertView.findViewById(R.id.list_sessions_amount);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.list_delete_btn);
//            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_discard_black);
//            drawable.setColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY);
//            viewHolder.deleteButton.setImageDrawable(drawable);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String folder = folders.get(position);
        viewHolder.folderName.setText(folder);
        viewHolder.numberSessions.setText("" + sessionCounts.get(position));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFolder(position);
            }
        });

        return convertView;
    }

    private void deleteFolder(final int position) {
        final String folderName = folders.get(position);
        String message = "The default folder cannot be deleted. Delete all sessions from this folder?";
        if (!"Default".equals(folderName)) {
            message = "Are you sure you want to delete this folder? (Data inside the folder will be lost!)";
        }

        new MaterialDialog.Builder(context)
                .title("Delete " + folderName + "?")
                .content(message)
                .negativeText("Cancel")
                .positiveText("Yes")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        FileHandler.deleteFolder(folderName);
                        if (!"Default".equals(folderName)) {
                            folders.remove(position);
                            sessionCounts.remove(position);
                        } else {
                            sessionCounts.set(position, 0);
                        }

                        notifyDataSetChanged();
                    }
                })
                .show();
    }
}

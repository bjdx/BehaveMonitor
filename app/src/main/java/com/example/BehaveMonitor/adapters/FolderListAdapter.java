package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.BehaveMonitor.R;

import java.util.List;

public class FolderListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> folders;
    private List<Integer> sessionCounts;

    public FolderListAdapter(Context context, List<String> folders, List<Integer> sessionCounts) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.folders = folders;
        this.sessionCounts = sessionCounts;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.folder_list_item, parent, false);

            viewHolder.folderName = (TextView) convertView.findViewById(R.id.list_folder_name);
            viewHolder.numberSessions = (TextView) convertView.findViewById(R.id.list_sessions_amount);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.list_delete_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String folder = folders.get(position);
        viewHolder.folderName.setText(folder);
        viewHolder.numberSessions.setText("" + sessionCounts.get(position));

        return convertView;
    }
}

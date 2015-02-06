package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.BehaveMonitor.R;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> events;

    public HistoryAdapter(Context context, List<String> events) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.events = events;
    }

    public void addEvent(String event) {
        events.add(0, event);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView name;
        Button amendBtn;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
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
            convertView = inflater.inflate(R.layout.session_history_element, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.history_event_name);
            viewHolder.amendBtn = (Button) convertView.findViewById(R.id.history_amend_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String event = events.get(position);
        viewHolder.name.setText(event);

        return convertView;
    }
}

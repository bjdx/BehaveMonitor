package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.BehaveMonitor.Event;
import com.example.BehaveMonitor.R;

import java.util.LinkedList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Event> events;
    private List<String> eventNames;

    public HistoryAdapter(Context context, List<String> eventNames) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        events = new LinkedList<>();
        this.eventNames = eventNames;
    }

    public void addEvent(Event event, String name) {
        events.add(0, event);
        eventNames.add(0, name);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView name;
        Button amendBtn;
    }

    @Override
    public int getCount() {
        return eventNames.size();
    }

    @Override
    public Object getItem(int position) {
        return eventNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.session_history_element, parent, false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.history_event_name);
            viewHolder.amendBtn = (Button) convertView.findViewById(R.id.history_amend_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Event event = events.get(position);
        String eventName = eventNames.get(position);
        viewHolder.name.setText(eventName);

        String mark = event.getMark() ? "Unmark" : "Mark";
        viewHolder.amendBtn.setText(mark);

        viewHolder.amendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.toggleMark();
                if (event.getMark()) {
                    viewHolder.amendBtn.setText("Unmark");
                } else {
                    viewHolder.amendBtn.setText("Mark");
                }
            }
        });

        return convertView;
    }
}

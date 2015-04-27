package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.BehaveMonitor.Behaviour;
import com.example.BehaveMonitor.BehaviourType;
import com.example.BehaveMonitor.R;

import java.util.List;

public class BehaviourListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Behaviour> behaviours;

    public BehaviourListAdapter(Context context, List<Behaviour> behaviours) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.behaviours = behaviours;
    }

    public void deleteBehaviour(int position) {
        this.behaviours.remove(position);
        notifyDataSetChanged();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return behaviours.size();
    }

    @Override
    public Object getItem(int position) {
        return behaviours.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.behaviour_list_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.list_behaviour_name);
            viewHolder.type = (TextView) convertView.findViewById(R.id.list_behaviour_type);
            viewHolder.deleteBtn = (ImageButton) convertView.findViewById(R.id.list_behaviour_delete_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Behaviour behaviour = (Behaviour) getItem(position);
        viewHolder.name.setText(behaviour.getName());
        viewHolder.type.setText(behaviour.getType() == BehaviourType.EVENT ? "Event" : "State");
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBehaviour(position);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        public TextView name, type;
        public ImageButton deleteBtn;
    }
}

//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Setting;
import com.example.BehaveMonitor.SettingsItem;

import java.util.List;

public class SettingsListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SettingsItem> items;

    public SettingsListAdapter(Context context, List<SettingsItem> items) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    public Setting getSetting(int position) {
        return items.get(position).getSetting();
    }

    private class ViewHolder {
        TextView heading, subheading;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
            convertView = inflater.inflate(R.layout.settings_list_item, parent, false);

            viewHolder.heading = (TextView) convertView.findViewById(R.id.settings_header);
            viewHolder.subheading = (TextView) convertView.findViewById(R.id.settings_subheader);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SettingsItem item = (SettingsItem) getItem(position);
        viewHolder.heading.setText(item.getHeading());
        viewHolder.subheading.setText(item.getSubheading());

        return convertView;
    }
}

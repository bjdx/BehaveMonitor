// Copyright (c) 2015 Barney Dennis & Gareth Lewis.

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.BehaveMonitor.R;

import java.util.List;

public class TemplateListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> templates;

    public TemplateListAdapter(Context context, List<String> templates) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.templates = templates;
    }

    private class ViewHolder {
        TextView templateName;
    }

    @Override
    public int getCount() {
        return templates.size();
    }

    @Override
    public Object getItem(int position) {
        return templates.get(position);
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

            convertView = inflater.inflate(R.layout.template_list_item, parent, false);
            viewHolder.templateName = (TextView) convertView.findViewById(R.id.list_template_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String template = templates.get(position);
        viewHolder.templateName.setText(template);

        return convertView;
    }
}

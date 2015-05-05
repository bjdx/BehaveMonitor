//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.BehaveMonitor.ObjectDrawerItem;
import com.example.BehaveMonitor.R;

public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

    private LayoutInflater inflater;
	private int layoutResourceId;
	private ObjectDrawerItem data[] = null;

	/*
	 * @mContext - app context
	 * 
	 * @layoutResourceId - the listview_item_row.xml
	 * 
	 * @data - the ListItem data
	 */
	public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, ObjectDrawerItem[] data) {
		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(layoutResourceId, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textViewName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

		ObjectDrawerItem folder = data[position];

        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(folder.icon, 0, 0, 0);
		viewHolder.textView.setText(folder.name);
		
		return convertView;
	}

    private class ViewHolder {
        TextView textView;
    }
}
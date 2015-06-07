//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.BehaveMonitor.FileHandler;
import com.example.BehaveMonitor.R;
import com.example.BehaveMonitor.Template;
import com.example.BehaveMonitor.TemplateActivity;

import java.util.List;

public class TemplateManageListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> templates;

    public TemplateManageListAdapter(Context context, List<String> templates) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.templates = templates;
    }

    private class ViewHolder {
        TextView templateName;
        ImageButton deleteButton, editButton;
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
            viewHolder.editButton = (ImageButton) convertView.findViewById(R.id.list_template_edit_button);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.list_template_delete_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String template = templates.get(position);
        viewHolder.templateName.setText(template);
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTemplate(position);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTemplate(position);
            }
        });

        return convertView;
    }

    private void editTemplate(int position) {
        String templateName = templates.get(position);
        Template template = new Template(FileHandler.readTemplate(templateName));

        Intent intent = new Intent(context, TemplateActivity.class);
        intent.putExtra("template", template);
        intent.putExtra("fromFragment", true);

        context.startActivity(intent);
    }

    private void deleteTemplate(final int position) {
        final String templateName = templates.get(position);

        new MaterialDialog.Builder(context)
                .title("Delete " + templateName + "?")
                .content("Are you sure you want to delete this template? (Template will be lost forever!)")
                .negativeText("Cancel")
                .positiveText("Yes")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        FileHandler.deleteTemplate(templateName);
                        templates.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .show();
    }
}

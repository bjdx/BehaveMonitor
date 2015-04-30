// Copyright (c) 2015 Barney Dennis & Gareth Lewis.

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Observation implements Parcelable {
    private List<Template> templates;

    public Observation() {
        this.templates = new ArrayList<>();
    }

    public Observation(Parcel in) {
        this(); // Calls default constructor to initialise list.

        in.readTypedList(templates, Template.CREATOR);
    }

    public Template get(int position) {
        position--; // Position is 1-indexed.
        if (templates != null && templates.size() > position) {
            return templates.get(position);
        }

        return null;
    }

    public int getCount() {
        return this.templates.size();
    }

//    public void setTemplates(List<Template> templates) {
//        this.templates = templates;
//    }

    public void addTemplate(Template template) {
        if (templates == null) {
            templates = new ArrayList<>();
        }

        templates.add(template);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(templates);
    }

    public static final Parcelable.Creator<Observation> CREATOR = new Parcelable.Creator<Observation>() {

        public Observation createFromParcel(Parcel in) {
            return new Observation(in);
        }

        public Observation[] newArray(int size) {
            return new Observation[size];
        }
    };
}

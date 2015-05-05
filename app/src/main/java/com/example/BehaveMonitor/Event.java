//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Event implements Parcelable{

	Date startTime;
	String duration = "";
    boolean marked = false;
    String note = "";
	
	Event() {
		startTime = new Date();
	}

    Event(Parcel in) {
        startTime = new Date(in.readLong());
        duration = in.readString();

        boolean[] parcelArray = new boolean[1];
        in.readBooleanArray(parcelArray);
        marked = parcelArray[0];

        note = in.readString();
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void toggleMark() {
        marked = !marked;
    }

    public boolean getMark() {
        return marked;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

	// Calculates the duration of a state event. Stores it in the format SS.sss
	public void end() {
		long diff = System.currentTimeMillis() - startTime.getTime();
        int seconds = (int)diff / 1000;
        diff -= seconds * 1000;

        duration = "" + seconds + ".";
        int length = ("" + diff).length();

        // Pad milliseconds with leading 0s until 3 digits.
        for (int i = 0; i < 3 - length; i++) {
            duration += "0";
        }

        duration += diff;
	}

    // Stuff to make it parcelable.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTime.getTime());
        dest.writeString(duration);
        dest.writeBooleanArray(new boolean[] {marked});
        dest.writeString(note);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}

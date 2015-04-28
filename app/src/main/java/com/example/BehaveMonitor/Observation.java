//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Observation implements Parcelable {
    String name;
    ArrayList<Behaviour> behaviours = new ArrayList<>();

    public Observation(){}

    public Observation(Parcel in) {
        // BUILD FROM PARCEL
        name = in.readString();
        behaviours = new ArrayList<>();
        in.readTypedList(behaviours, Behaviour.CREATOR);
    }

    /**
     * Creates an observation from a string
     * @param string the string representation of an observation
     */
    public Observation(String string) {
        if (string == null) {
            return;
        }

    	String[] namePart = string.split(";");
    	if (namePart.length > 1) {
	    	name = namePart[0];
	    	String[] behaviours = namePart[1].split(":");
	    	if (behaviours.length > 0) {
		    	for (String b : behaviours) {
		    		String[] bParts = b.split(",");
		    		if (bParts.length > 1) {
			    		Behaviour be = new Behaviour();
			    		be.bName = bParts[0];
			    		be.type = Integer.parseInt(bParts[1]);
			    		this.behaviours.add(be);
		    		} else {
		    			Log.e("Observation missing data:", "Behaviour name or type.");
		    		}
		    	}
	    	} else {
    			Log.e("Observation missing data:", "No behaviours in observation.");
	    	}
    	} else {
			Log.e("Observation missing data:", "Name or behaviour.");
    	}
    }
    
    // Outputs the observations name and all behaviours and types for saving.
    public String toString() {
    	String out = name + ";";
    	for(Behaviour b : behaviours) {
    		out += b.bName + ",";
    		out += b.type + ":";
    	}
    	
		return out;
    }

    /**
     * Determines if the observation has any behaviours added
     * @return true if the observation has no behaviours, false otherwise
     */
    public boolean isEmpty() {
    	return behaviours.isEmpty();
    }

    // Stuff to make it parcelable.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(behaviours);
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

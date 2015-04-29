//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Template implements Parcelable {
    String name;
    ArrayList<Behaviour> behaviours = new ArrayList<>();

    public Template(){}

    public Template(Parcel in) {
        // BUILD FROM PARCEL
        name = in.readString();
        behaviours = new ArrayList<>();
        in.readTypedList(behaviours, Behaviour.CREATOR);
    }

    /**
     * Creates an template from a string
     * @param string the string representation of an template
     */
    public Template(String string) {
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
		    			Log.e("Template missing data:", "Behaviour name or type.");
		    		}
		    	}
	    	} else {
    			Log.e("Template missing data:", "No behaviours in template.");
	    	}
    	} else {
			Log.e("Template missing data:", "Name or behaviour.");
    	}
    }
    
    // Outputs the template name and all behaviours and types for saving.
    public String toString() {
    	String out = name + ";";
    	for(Behaviour b : behaviours) {
    		out += b.bName + ",";
    		out += b.type + ":";
    	}
    	
		return out;
    }

    /**
     * Determines if the template has any behaviours added
     * @return true if the template has no behaviours, false otherwise
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

    public static final Parcelable.Creator<Template> CREATOR = new Parcelable.Creator<Template>() {

        public Template createFromParcel(Parcel in) {
            return new Template(in);
        }

        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

}

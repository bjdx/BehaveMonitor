//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by BJD on 06/12/2014.
 */
public class Behaviour implements Parcelable {
	//Behaviour Name
	String bName;
	
	//0 = Event
	//1 = State
	int type;
	
	//Holds the events for this behaviour.
	ArrayList<Event> eventHistory = new ArrayList<>();
	
	//Holds the current state event before its ended and added to the history.
	Event currentEvent = null;

    Behaviour(){}

    Behaviour(Parcel in) {
        bName = in.readString();
        type = in.readInt();
        eventHistory = new ArrayList<>();
        in.readTypedList(eventHistory,Event.CREATOR);
        currentEvent = in.readParcelable(Event.class.getClassLoader());
    }

    public static final Parcelable.Creator<Behaviour> CREATOR = new Parcelable.Creator<Behaviour>() {

        public Behaviour createFromParcel(Parcel in) {
            return new Behaviour(in);
        }

        public Behaviour[] newArray(int size) {
            return new Behaviour[size];
        }
    };

    public String getName() {
        return bName;
    }

    public int getType() {
        return type;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public boolean isMarked() {
        for (Event e : eventHistory) {
            if (e.getMark()) {
                return true;
            }
        }

        return false;
    }

    public void newEvent() {
		if (this.type == BehaviourType.EVENT) {
			Event event = new Event();
			event.duration = "";
			eventHistory.add(event);
		} else if (this.type == BehaviourType.STATE) {
			currentEvent = new Event();
		} else {
			Log.e("Event type error", "the eType was not a 1 or a 0");
			//ERROR
		}
	}
	
	//Ends the current event of this behaviour type, and adds it to the history reseting the currentEvent variable.
	public void endCurrentEvent() {
		if (currentEvent != null) {
			currentEvent.end();
			eventHistory.add(currentEvent);
			currentEvent = null;
		}
	}

    //returns the last event added to the event history.
    public Event getLastEvent() {
        if (!eventHistory.isEmpty()) return eventHistory.get(eventHistory.size() - 1);
        else return null;
    }

	//Returns the behaviour and all its events for session Saving
	public String toString() {
		//Get name and type of behaviour
		String out = this.bName + "/nType = " + this.type + ":\n\n";
		SimpleDateFormat sdf = new SimpleDateFormat("H.m.s.S");
		
		//If type0 Event just the start time
		String startTimes = "Start Times:";
		if (this.type == BehaviourType.EVENT) {
			for (Event e: this.eventHistory) {
                String mark = e.getMark() ? "m" : "";
				startTimes += "," + sdf.format(e.startTime) + mark;
			}
		
		//else type1 state durations and start times.
		} else if (this.type == BehaviourType.STATE) {
			String durations = "Durations:";
			for (Event e: this.eventHistory) {
                String mark = e.getMark() ? "m" : "";
				startTimes += "," + sdf.format(e.startTime) + mark;
				durations += "," + e.duration + mark;
			}
			durations += "/n";
			out += durations;
		} else {
			Log.e("Event type error", "the eType was not a 1 or a 0");
			//ERROR
		}
		startTimes += "/n";
		out += startTimes;
		return out;
	}



    //Stuff to make it parcelable.


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bName);
        dest.writeInt(type);
        dest.writeTypedList(eventHistory);
        dest.writeParcelable(currentEvent, flags);

    }
}

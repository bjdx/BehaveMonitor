//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Behaviour implements Parcelable {
	private String name;
	private int type; // Behaviour type

	private ArrayList<Event> eventHistory = new ArrayList<>(); // Holds the events for this behaviour.
	private Event currentEvent = null; // Holds the active state event.

    private boolean active = false; // If this behaviour is currently active.
    private boolean separate; // If this is true, this behaviour should not toggle other behaviours on/off

    public Behaviour(){}

    public Behaviour(Parcel in) {
        name = in.readString();
        type = in.readInt();
        separate = in.readInt() == 1;
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

    /**
     * Gets the name of a behaviour
     * @return a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of a behaviour
     * @param name the string to change the behaviour's name to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of a behaviour.
     * @return an integer.
     * @see com.example.BehaveMonitor.BehaviourType
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of a behaviour
     * @param type the integer representation of the type to change the behaviour to
     * @see com.example.BehaveMonitor.BehaviourType
     */
    public void setType(int type) {
        this.type = type;
    }

    public boolean isSeparate() {
        return separate;
    }

    public void setSeparate(boolean separate) {
        this.separate = separate;
    }

    /**
     * Gets the event history for a behaviour
     * @return an {@code ArrayList<Event>}
     */
    public ArrayList<Event> getEventHistory() {
        return eventHistory;
    }

    /**
     * Gets the currently active event
     * @return an Event or null if no event is active
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }

    /**
     * Gets if any of the events for this behaviour have a mark
     * @return true if one or more events have a mark, false otherwise.
     */
    public boolean isMarked() {
        for (Event e : eventHistory) {
            if (e.getMark()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets whether there is an active behaviour.
     * @return true if there is an active behaviour, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    public void newEvent() {
		if (this.type == BehaviourType.EVENT) {
			Event event = new Event();
			event.duration = "";
			eventHistory.add(event);
		} else if (this.type == BehaviourType.STATE) {
			currentEvent = new Event();
            active = true;
		} else {
			Log.e("Event type error", "the eType was not a 1 or a 0");
			//ERROR
		}
	}

    /**
     * 1. Ends the current event of this behaviour type
     * 2. Adds the event to the history
     */
	public void endCurrentEvent() {
		if (currentEvent != null) {
			currentEvent.end();
			eventHistory.add(currentEvent);
            active = false;
			currentEvent = null;
		}
	}

    /**
     * Gets the last event added to the event history.
     * @return an event.
     */
    public Event getLastEvent() {
        if (!eventHistory.isEmpty()) return eventHistory.get(eventHistory.size() - 1);
        else return null;
    }

	//Returns the behaviour and all its events for session Saving
//	public String toString() {
//		//Get name and type of behaviour
//		String out = this.name + "/nType = " + this.type + ":\n\n";
//		SimpleDateFormat sdf = new SimpleDateFormat("H.m.s.S");
//
//		//If type0 Event just the start time
//		String startTimes = "Start Times:";
//		if (this.type == BehaviourType.EVENT) {
//			for (Event e: this.eventHistory) {
//                String mark = e.getMark() ? "m" : "";
//				startTimes += "," + sdf.format(e.startTime) + mark;
//			}
//
//		//else type1 state durations and start times.
//		} else if (this.type == BehaviourType.STATE) {
//			String durations = "Durations:";
//			for (Event e: this.eventHistory) {
//                String mark = e.getMark() ? "m" : "";
//				startTimes += "," + sdf.format(e.startTime) + mark;
//				durations += "," + e.duration + mark;
//			}
//			durations += "/n";
//			out += durations;
//		} else {
//			Log.e("Event type error", "the eType was not a 1 or a 0");
//			//ERROR
//		}
//		startTimes += "/n";
//		out += startTimes;
//		return out;
//	}



    //Stuff to make it parcelable.


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeInt(separate ? 1 : 0);
        dest.writeTypedList(eventHistory);
        dest.writeParcelable(currentEvent, flags);
    }
}

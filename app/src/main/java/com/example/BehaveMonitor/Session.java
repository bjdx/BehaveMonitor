//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//Session Storage Format:
/*
Session Name,@name
Start Time,##.##.##
End Time,##.##.##
Session Location,@location
Template Name,@template.name

Event Behaviours


@eBehaviour1.name

Start Times
@eBehaviour1.e1,@eBehaviour1.e2,...

@eBehaviour2.name

Start Times
@eBehaviour2.e1,@eBehaviour2.e2,...

State Behaviours


@sBehaviour1.name

Start Times
@sBehaviour1.e1,@sBehaviour1.e2,...

Durations
@sBehaviour1.d1,@sBehaviour1.d2,...

@sBehaviour2.name
@sBehaviour2.e1,@sBehaviour2.e2,...

Durations
@sBehaviour1.d1,@sBehaviour1.d2,...

 */

public class Session implements Parcelable {
    //Name of the session
    private String name;
    //Date and time the session was started
    private List<Date> startTime = new ArrayList<>();
    //Date and time the session was started
    private List<Date> endTime = new ArrayList<>();
    //The location of the session
    private String location;
    //The behaviour template the session used
//    private Template template;
    //The path to the folder the session will be saved to
    private String path;

    private Observation observations;

    /**
     * Constructor initialising the name, location and path
     * @param name name of the session
     * @param location location of the session
     * @param path path to the folder selected
     */
    public Session(String name, String location, String path) {
        this.name = name;
        this.location = location;
        this.path = path;
    }

    public Session(Parcel in) {
        this.name = in.readString();
        Long tmpTime = in.readLong();
        if (tmpTime != 0) {
            this.startTime.add(new Date(tmpTime));
        }

        tmpTime = in.readLong();
        if (tmpTime != 0) {
            this.endTime.add(new Date(tmpTime));
        }

        this.location = in.readString();

        // readParcelable need class loader
//        this.template = in.readParcelable(Template.class.getClassLoader());
        this.observations = in.readParcelable(Observation.class.getClassLoader());
        this.path = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

//    public Template getTemplate() {
//        return template;
//    }

    public List<Behaviour> getBehaviours(int observation) {
        return this.observations.get(observation).behaviours;
//        return this.template.behaviours;
    }

    public Template getTemplate(int observation) {
        return this.observations.get(observation);
    }

    public void setObservations(Observation observations) {
        this.observations = observations;
    }

    public int getObservationsCount() {
        return this.observations.getCount();
    }

    /**
     * Method for setting the behaviour template.
     * @param template the template to store
     */
//    public void setTemplate(Template template) {
//        this.template = template;
//    }

    /**
     * Sets the date and time of when the session began
     */
    public void startSession() {
        this.startTime.add(new Date());
    }

    /**
     * Sets the end date and time
     */
    public void endSession() {
        this.endTime.add(new Date());
    }

    public String toString(int observation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.UK);
        String out = "Session Name," + this.name + "\n";
        out += "Start Date,";
        out += sdf.format(startTime.get(observation - 1).getTime()) + ",";

//        String endString = "\nEnd Date,";
//        endString += endTime.size() <= observation - 1 ? "Autosave at " + sdf.format(new Date()) + "," : sdf.format(endTime.get(observation - 1).getTime()) + ",";

        out += "\nEnd Date," + (endTime.size() <= observation - 1 ? "Autosave at " + sdf.format(new Date()) + "," : sdf.format(endTime.get(observation - 1).getTime())) + ",";
        out += "\nSession Location," + this.location + "\n";
        out += "Template Name," + this.observations.get(observation).name + "\n";

        // Split behaviour types.
        ArrayList<Behaviour> eBe = new ArrayList<>();
        ArrayList<Behaviour> sBe = new ArrayList<>();
        for (Behaviour b : this.observations.get(observation).behaviours) {
            if (b.type == BehaviourType.EVENT) {
                eBe.add(b);
            } else {
                sBe.add(b);
            }
        }

        if (eBe.size() > 0) {
            out += "\nEvent Behaviours\n\n";

            for (Behaviour b : eBe) {
                out += b.bName + "\n";
                String starts = "Start Times,";
                String notes = "Notes,";
                for (Event e : b.eventHistory) {
                    String mark = e.getMark() ? "m" : "";
                    starts += timeDiff(startTime.get(observation - 1), e.startTime) + mark + ",";
                    notes += e.getNote() + ",";
                }

                out += starts + "\n";
                out += notes + "\n\n";
            }
        } else {
            out += "\n";
        }

        if (sBe.size() > 0) {
            out += "State Behaviours\n\n";

            for (Behaviour b : sBe) {
                out += b.bName + "\n";
                String starts = "Start Times,";
                String duration = "Durations,";
                String notes = "Notes,";
                for (Event e : b.eventHistory) {
                    String mark = e.getMark() ? "m" : "";
                    starts += timeDiff(startTime.get(observation - 1), e.startTime) + mark + ",";
                    duration += e.duration + mark + ",";
                    notes += e.getNote() + ",";
                }

                out += starts + "\n";
                out += duration + "\n";
                out += notes + "\n\n";
            }
        }

        return out;
    }

    // Takes two dates and returns the difference in the format SS.sss 3dp.
    public String timeDiff(Date sT, Date eT) {

        long diff = eT.getTime() - sT.getTime();
        int seconds = (int)diff/1000;
        diff -= seconds*1000;

        String out = "" + seconds + ".";
        int length = ("" + diff).length();

        switch(length) {
            case(0):
                out += "000";
                break;
            case(1):
                out += "00" + diff;
                break;
            case(2):
                out += "0" + diff;
                break;
            case(3):
                out += ""+ diff;
                break;
            default:
                break;
        }
        return out;
    }

    /**
     * Returns a string of hours mins and secs since now Date.
     * @return an HH:MM:SS formatted time string.
     */
    public String getRelativeHMS(int observation) {
        long millis = new Date().getTime() - this.startTime.get(observation - 1).getTime();
        return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    // Stuff to make it parcelable.
    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * Adds contents of Session to parcel for Parcelisation in order to be entered into the
     * Parcelisation Matrix which allows it to be sent to the other activity...
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(startTime == null || startTime.size() == 0 ? 0 : startTime.get(0).getTime());
        dest.writeLong(endTime == null || startTime.size() == 0 ? 0 : endTime.get(0).getTime());
        dest.writeString(location);
//        dest.writeParcelable(template, flags);
        dest.writeParcelable(observations, flags);
        dest.writeString(path);
    }

    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {

        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}

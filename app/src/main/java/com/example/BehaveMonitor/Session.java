package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by BJD on 06/12/2014.
 */


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
    private Date startTime = new Date();
    //Date and time the session was started
    private Date endTime = null;
    //The location of the session
    private String location;
    //The behaviour template the session used
    private Template template;
    //The path to the folder the session will be saved to
    private String path;


    //Constructor initialising the name and location
    public Session(String name, String location, String path) {
        this.name = name;
        this.location = location;
        this.path = path;
    }

    public Session(Parcel in) {
        this.name = in.readString();
        Long tmpTime = in.readLong();
//        if (tmpTime != null)
        this.startTime = new Date(tmpTime);
        tmpTime = in.readLong();
        if (tmpTime != 0) {
            this.endTime = new Date(tmpTime);
        } else {
            this.endTime = null;
        }

        this.location = in.readString();

        // readParcelable need class loader
        this.template = in.readParcelable(Template.class.getClassLoader());
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

    public Template getTemplate() {
        return template;
    }

    public List<Behaviour> getBehaviours() {
        return this.template.behaviours;
    }

//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }

    //Method for setting the behaviour template.
    public void setTemplate(Template template) {
        this.template = template;
    }

    //Sets the date and time of when the session began
    public void startSession() {
        this.startTime = new Date();
    }

    //Sets then date and time of when the session ended
    public void endSession() {
        this.endTime = new Date();
    }

    /**
     * This returns the session in an output file friendly format.
     *
     * @return
     * This returns the session in an output file friendly format.
     */
    @Override
    public String toString() {
        String out = "Session Name," + this.name + "\n";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
        out += "Start Date," + sdf.format(startTime.getTime()) + "\n";
        out += "End Date," + ((endTime != null) ? sdf.format(endTime.getTime()) : "Autosave at " + sdf.format(new Date())) + "\n";

        out += "Session Location," + this.location + "\n";

        out += "Template Name," + this.template.name + "\n";

        //Split behaviour types.
        ArrayList<Behaviour> eBe = new ArrayList<>();
        ArrayList<Behaviour> sBe = new ArrayList<>();
        for (Behaviour b : this.template.behaviours) {
            if (b.type == BehaviourType.EVENT) {
                eBe.add(b);
            } else {
                sBe.add(b);
            }
        }

        out += "\nEvent Behaviours\n\n";

        for (Behaviour b : eBe) {
            out += b.bName + "\n";
            String starts = "Start Times,";
            String notes = "Notes,";
            for (Event e : b.eventHistory) {
                String mark = e.getMark() ? "m" : "";
                starts += timeDiff(startTime, e.startTime) + mark + ",";
                notes += e.getNote() + ",";
            }

            out += starts + "\n";
            out += notes + "\n";
        }

        out += "\nState Behaviours\n\n";

        for (Behaviour b : sBe) {
            out += b.bName + "\n";
            String starts = "Start Times,";
            String duration = "Durations,";
            String notes = "Notes,";
            for (Event e : b.eventHistory) {
                String mark = e.getMark() ? "m" : "";
                starts += timeDiff(startTime, e.startTime) + mark + ",";
                duration += e.duration + mark + ",";
                notes += e.getNote() + ",";
            }

            out += starts + "\n";
            out += duration + "\n";
            out += notes + "\n";
        }
        return out;
    }

    //Takes two dates and returns the difference in the format SS.sss 3dp.
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

    //Returns a string of hours mins and secs since now Date.
    public String getRelativeHMS(Date now) {
        long millis = now.getTime() - this.startTime.getTime();
        return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    //Stuff to make it parcelable.
    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * Adds contents of Session to parcel for Parcelisation in order to be entered into the
     * Parcelisation Matrix which allows it to be sent to the other activity...
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(startTime.getTime());
        dest.writeLong(endTime == null ? 0 : endTime.getTime());
        dest.writeString(location);
        dest.writeParcelable(template,flags);
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

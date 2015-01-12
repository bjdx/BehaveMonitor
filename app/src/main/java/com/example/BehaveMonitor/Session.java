package com.example.BehaveMonitor;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    String name;
    //Date and time the session was started
    Date startTime = new Date();
    //Date and time the session was started
    Date endTime = new Date();
    //The location of the session
    String location;
    //The behaviour template the session used
    Template template;
    //The path to the folder the session will be saved to
    String path;


    //Constructor initialising the name and location
    Session(String name, String location, String path) {
        this.name = name;
        this.location = location;
        this.path = path;
    }

    public Session(Parcel in) {
        this.name = in.readString();
        Long tmpTime = in.readLong();
        if (tmpTime != null) this.startTime = new Date(tmpTime);
        tmpTime = in.readLong();
        if (tmpTime != null) this.endTime = new Date(tmpTime);
        this.location = in.readString();

        // readParcelable need class loader
        this.template = in.readParcelable(Template.class.getClassLoader());
        this.path = in.readString();
    }

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
        out += "End Date," + sdf.format(endTime.getTime()) + "\n";

        out += "Session Location," + this.location + "\n";

        out += "Template Name," + this.template.name + "\n";

        //Split behaviour types.
        ArrayList<Behaviour> eBe = new ArrayList<Behaviour>();
        ArrayList<Behaviour> sBe = new ArrayList<Behaviour>();
        for (Behaviour b : this.template.behaviours) {
            if (b.type == 0) {
                eBe.add(b);
            } else {
                sBe.add(b);
            }
        }

        out += "\nEvent Behaviours\n\n";

        for (Behaviour b : eBe) {
            out += b.bName + "\n";
            String starts = "Start Times\n";
            for (Event e : b.eventHistory) {
                starts += timeDiff(startTime, e.startTime) + ",";
            }
            out += starts + "\n";
        }

        out += "\nState Behaviours\n\n";

        for (Behaviour b : sBe) {
            out += b.bName + "\n";
            String starts = "Start Times\n";
            String duration = "Durations\n";
            for (Event e : b.eventHistory) {
                starts += timeDiff(startTime, e.startTime) + ",";
                duration += e.duration + ",";
            }
            out += starts + "\n";
            out += duration + "\n";
        }
        return out;
    }

    //Takes two dates and returns the difference in the format SS.sss 3dp.
    public String timeDiff(Date sT, Date eT) {

        long diff = eT.getTime() - sT.getTime();
        int seconds = (int)diff/1000;
        diff -= seconds*1000;

        String out = "" + seconds + ".";
        int length = new String(""+diff).length();

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
        long diff = now.getTime() - this.startTime.getTime();
        Date out = new Date(diff);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(out);
    }


    //Stuff to make it parcelable.

    @Override
    public int describeContents() {
        return 0;
    }


    //Adds contents of Session to parcel for Parcelisation in order to be entered into the
    //Parcelisation Matrix which allows it to be sent to the other activity...
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(startTime.getTime());
        dest.writeLong(endTime.getTime());
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

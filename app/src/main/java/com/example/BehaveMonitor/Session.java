package com.example.BehaveMonitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
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
public class Session {
    //Name of the session
    String name;
    //Date and time the session was started
    Calendar startTime;
    //Date and time the session was started
    Calendar endTime;
    //The location of the session
    String location;
    //The behaviour template the session used
    Template template;


    //Constructor initialising the name and location
    Session(String name, String location) {
        this.name = name;
        this.location = location;
    }

    //Method for setting the behaviour template.
    public void setTemplate(Template template) {
        this.template = template;
    }

    //Sets the date and time of when the session began
    public void startSession() {
        this.startTime = Calendar.getInstance();
    }

    //Sets then date and time of when the session ended
    public void endSession() {
        this.endTime = Calendar.getInstance();
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
                starts += timeDiff(startTime.getTime(), e.startTime) + ",";
            }
            out += starts + "\n";
        }

        out += "\nState Behaviours\n\n";

        for (Behaviour b : sBe) {
            out += b.bName + "\n";
            String starts = "Start Times\n";
            String duration = "Durations\n";
            for (Event e : b.eventHistory) {
                starts += timeDiff(startTime.getTime(), e.startTime) + ",";
                duration += e.duration + ",";
            }
            out += starts + "\n";
            out += duration + "\n";
        }
        return out;
    }

    //Takes two dates and returns the difference in the format SS.sss
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
            default:
                out+= "inv";
                break;
        }
        return out;
    }
}

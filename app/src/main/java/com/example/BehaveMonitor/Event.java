package com.example.BehaveMonitor;

import java.util.Date;

/**
 * Created by BJD on 06/12/2014.
 */
public class Event {

	Date startTime;
	String duration;

	
	Event() {
		startTime = new Date();
	}
	
	
	//Calculates the duration of a state event. Stores it in the format SS.sss
	public void end() {
		long diff = System.currentTimeMillis() - startTime.getTime();
        int seconds = (int)diff / 1000;
        diff -= seconds * 1000;

        duration = "" + seconds + ".";
        int length = new String("" + diff).length();

        switch(length) {
            case(0):
                duration += "000";
                break;
            case(1):
                duration += "00" + diff;
                break;
            case(2):
                duration += "0" + diff;
                break;
            case(3):
                duration += ""+ diff;
            default:
                duration += "inv";
                break;
        }
	}
	
}

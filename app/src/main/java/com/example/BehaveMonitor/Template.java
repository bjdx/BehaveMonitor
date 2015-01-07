package com.example.BehaveMonitor;

import java.util.ArrayList;

import android.util.Log;

/**
 * Created by BJD on 06/12/2014.
 */
public class Template {
    String name;
    ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();
    
    Template(){}
    
    Template(String string) {
    	String[] namePart = string.split(";");
    	if(namePart.length>1) {
	    	name = namePart[0];
	    	String[] behaviours = namePart[1].split(":");
	    	if(behaviours.length>0) {
		    	for(String b:behaviours) {
		    		String[] bParts = b.split(",");
		    		if(bParts.length>1) {
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
    
    //Outputs the templates name and all behaviours and types for saving.
    public String toString() {
    	String out = name+";";
    	for(Behaviour b : behaviours) {
    		out += b.bName+",";
    		out += b.type+":";
    	}
    	
		return out;
    	
    }
    
    public boolean isEmpty() {
    	return behaviours.isEmpty();
    }
}

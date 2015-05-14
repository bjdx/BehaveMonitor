//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

public class Setting {
    public static final int EMAIL = 0;
    public static final int DEFAULT_OBSERVATIONS = 1;
    public static final int MAX_OBSERVATIONS = 2;
    public static final int SHOW_RENAME_DIALOG = 3;
    public static final int NAME_PREFIX = 4;

    private int setting;

    private String heading = "";
    private String subheading = "";

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }

    public int getSetting() {
        return setting;
    }

    public void setSetting(int setting) {
        this.setting = setting;
    }
}

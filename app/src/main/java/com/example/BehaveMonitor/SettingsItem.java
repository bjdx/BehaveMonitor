// Copyright (c) 2015 Barney Dennis & Gareth Lewis.

package com.example.BehaveMonitor;

public class SettingsItem {
    private Setting setting;

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

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}

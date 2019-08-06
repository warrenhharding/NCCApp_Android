package com.salzburg101.nccapp;

import android.app.Application;

// This class creates Global Variables which are then available for use throughout the application. Initially
// setup primarily to set the numbers of unread messages / alerts etc on Badges.


public class createGlobalVariables extends Application {

    // Create the counter for Alerts

    private Integer unreadAlerts;

    public Integer getUnreadAlerts() {
        return unreadAlerts;
    }

    public void setUnreadAlerts(Integer unreadAlerts) {
        this.unreadAlerts = unreadAlerts;
    }


    // Create the counter for MyClass News

    private Integer unreadNews;

    public Integer getUnreadNews() {
        return unreadNews;
    }

    public void setUnreadNews(Integer unreadNews) {
        this.unreadNews = unreadNews;
    }

}

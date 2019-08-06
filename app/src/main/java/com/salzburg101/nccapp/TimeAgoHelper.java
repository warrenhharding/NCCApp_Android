package com.salzburg101.nccapp;

import android.os.Build;

public class TimeAgoHelper {

    public static String getTimeAgo(long dateToChange) {

        String intervalDescription = "";

        Double secondsInAnHour = 3600.0;
        Double secondsInDays = 86400.0;
        Double secondsInWeek = 604800.0;

        if (dateToChange < secondsInAnHour) {
            long x = Math.round(dateToChange/60);

            if (x == 1) {
                intervalDescription = x + " minute ago";
            } else {
                intervalDescription = x + " minutes ago";
            }
        } else if (dateToChange < secondsInDays) {
            long x = Math.round(dateToChange/(60*60));
            if (x == 1) {
                intervalDescription = x + " hour ago";
            } else {
                intervalDescription = x + " hours ago";
            }
        } else if (dateToChange < secondsInWeek) {
            long x = Math.round(dateToChange/(60*60*24));
            if (x == 1) {
                intervalDescription = x + " day ago";
            } else {
                intervalDescription = x + " days ago";
            }
        } else if (dateToChange > secondsInWeek) {
            intervalDescription = "NULL";
        }

        return intervalDescription;

    }


}

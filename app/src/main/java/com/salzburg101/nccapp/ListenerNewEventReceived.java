package com.salzburg101.nccapp;

// The purpose of this interface is to setup a listening device within an Activity, which detects when a new alert has been received.
// When the alert is received, the Firebase listener in RetrieveAlerts.java is triggered. This receives the new alert (and the old ones).
// This updates the globally available table alertsSummary (defined in DefineAlertSummary). This calls a method in ProcessBadges called
// setBadgeNumbersAlerts(). This method compares the number of alerts available with the number of alerts read (information contained in
// the globally available alertsArray ArrayList (defined in DefineAlertsArray) and decides how many are 'unread'. It then attempts to set
// the badge to that number. However, if a different activity is open (not the RetrieveAlerts activity), then the badge won't be upated
// (because it only tries to update the badge in RetrieveAlerts). Therefore, the listeners in the other activities are listening out for
// the update.

// The code in ths class below creates the listener.

// ProcessBadges has the following pieces of code:

// This is where the listener is initialised and belongs where the class is being defined.
//private static ListenerNewAlertReceived mListener;
//
//public static void setListenerNewAlert(ListenerNewAlertReceived eventListener) {
//        mListener = eventListener;
//        }


// Then again in ProcessBadges, the piece of code below is called to inform the listener that something has happened and it shoudl be triggered.

//if(mListener!=null) mListener.onEvent();

// Finally, the activity listening out for the event being triggered needs to be setup as follows:

// This belongs in the onCreate() method of the activity and determines what should happen when the listener is triggered.
//ProcessBadges.setListenerNewAlert(new ListenerNewAlertReceived() {
//@Override
//public void onEvent() {
//        BottomNavigationView navBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        navBar.showBadge(R.id.action_alerts).setNumber(57);
//        }
//        });



public interface ListenerNewEventReceived {
    public void onEvent();
}

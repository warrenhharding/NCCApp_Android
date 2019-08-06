package com.salzburg101.nccapp;

public class MyFirebaseMessagingService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    private static final String TAG = "MyFirebaseMessagingService";

    // @Override
    public void onNewToken(String token) {

        // Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }



    public void sendRegistrationToServer(String token) {
        // TODO Work required here to update the CustomerDatabase if the token changes.
        System.out.println("Just sent registration token to the server");
    }

}

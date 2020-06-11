package com.ramith.ascentic.centicbids.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//Initializing the FirebaseMessagingService to receive FCM push messages
class CBFirebaseInstanceIDService : FirebaseMessagingService(){

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if(p0.notification != null){
            Log.d("FCM_AUTO", p0.data.toString())
        }

    }


}
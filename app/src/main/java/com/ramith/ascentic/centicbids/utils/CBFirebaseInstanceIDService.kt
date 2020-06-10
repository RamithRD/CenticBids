package com.ramith.ascentic.centicbids.utils

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CBFirebaseInstanceIDService : FirebaseMessagingService(){

    override fun onNewToken(token : String?) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(newMessage : RemoteMessage?) {
        super.onMessageReceived(newMessage)
    }


}
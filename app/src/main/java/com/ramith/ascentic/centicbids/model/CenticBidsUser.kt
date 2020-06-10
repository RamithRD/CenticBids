package com.ramith.ascentic.centicbids.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

class CenticBidsUser() : Serializable {

    var userId: String? = null
    var  email: String? = null
    var  fcm_token: String? = null

    @Exclude
    var isAuthenticated : Boolean = false

    @Exclude
    var isCreated : Boolean = false



}
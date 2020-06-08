package com.ramith.ascentic.centicbids.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

class CenticBidsUser() : Serializable {

    var isAuthenticated : Boolean = false

    var userId: String? = null

    var  email: String? = null


}
package com.ramith.ascentic.centicbids.feature.authentication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.ramith.ascentic.centicbids.model.CenticBidsUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreDbRef = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestoreDbRef.collection("registered_users")

    //signin a user
    fun loginUser(email : String, password : String): MutableLiveData<CenticBidsUser>? {

        val authenticatedUserMutableLiveData = MutableLiveData<CenticBidsUser>()

        if(email.isNotEmpty() && password.isNotEmpty()){

            CoroutineScope(Dispatchers.IO).launch {

                try{

                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
                    val firebaseUser = firebaseAuth.currentUser
                    if(firebaseUser == null){
                        //user not logged in
                        Log.d("BIDS_AUTH", "user NOT logged in")
                        val user = CenticBidsUser()
                        user.isAuthenticated = false
                        authenticatedUserMutableLiveData.postValue(user)
                    }else {
                        //user is logged in
                        Log.d("BIDS_AUTH", "user is logged in")
                        val uid: String = firebaseUser.getUid()
                        val email: String = firebaseUser.getEmail()!!

                        val user = CenticBidsUser()
                        user.userId = uid
                        user.email = email
                        user.isAuthenticated = true
                        authenticatedUserMutableLiveData.postValue(user)
                    }
                }catch (e : Exception){
                    Log.d("REG_ERROR", e.message)
                    val user = CenticBidsUser()
                    user.statusMsg = e.message
                    user.isAuthenticated = false
                    authenticatedUserMutableLiveData.postValue(user)
                }

            }

        }

        return authenticatedUserMutableLiveData
    }

    //signup a user
    fun registerUser(email : String, password : String, fcmToken : String): MutableLiveData<CenticBidsUser>? {

        val registeredUserMutableLiveData = MutableLiveData<CenticBidsUser>()

        if(email.isNotEmpty() && password.isNotEmpty()){

            CoroutineScope(Dispatchers.IO).launch {

                try{

                    val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                        //val isNewUSer = result.additionalUserInfo?.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if(firebaseUser == null){
                        //user not logged in
                        Log.d("BIDS_AUTH", "user NOT registered")
                        val user = CenticBidsUser()
                        user.isAuthenticated = false
                        registeredUserMutableLiveData.postValue(user)
                    }else {
                        //user is logged in
                        Log.d("BIDS_AUTH", "user is registered")
                        val uid: String = firebaseUser.getUid()
                        val email: String = firebaseUser.getEmail()!!

                        val user = CenticBidsUser()
                        user.userId = uid
                        user.email = email
                        user.fcm_token = fcmToken
                        user.isAuthenticated = true
                        registeredUserMutableLiveData.postValue(user)
                    }
                }catch (e : Exception){
                    Log.d("REG_ERROR", e.message)
                    val user = CenticBidsUser()
                    user.statusMsg = e.message
                    user.isAuthenticated = false
                    registeredUserMutableLiveData.postValue(user)
                }

            }

        }

        return registeredUserMutableLiveData
    }

    //create a user record in Firestore to store fcm token and other user details
    fun createUserInFirestoreIfNotExists(authenticatedUser: CenticBidsUser): MutableLiveData<CenticBidsUser>? {

        val newUserMutableLiveData = MutableLiveData<CenticBidsUser>()
        val uidRef: DocumentReference = usersCollectionRef.document(authenticatedUser.userId!!)

        CoroutineScope(Dispatchers.IO).launch {

            try{

                uidRef.get().addOnCompleteListener { uidTask: Task<DocumentSnapshot?> ->

                    if (uidTask.isSuccessful) {

                        val document = uidTask.result

                        if (!document!!.exists()) {

                            uidRef.set(authenticatedUser)
                                .addOnCompleteListener { userCreationTask: Task<Void?> ->

                                    if (userCreationTask.isSuccessful) {
                                        authenticatedUser.isCreated = true
                                        newUserMutableLiveData.postValue(authenticatedUser)
                                    } else {
                                        Log.d("REG_ERROR", userCreationTask.exception!!.message)
                                    }
                                }
                        } else {
                            newUserMutableLiveData.postValue(authenticatedUser)
                        }

                    } else {
                        Log.d("REG_ERROR", uidTask.exception!!.message)
                    }

                }.await()

            }catch (e : Exception){
                Log.d("REG_ERROR", e.message)
            }

        }

        return newUserMutableLiveData
    }

    fun checkIfUserIsAuthenticatedInFirebase() : MutableLiveData<CenticBidsUser>? {

        val isUserAuthenticateInFirebaseMutableLiveData = MutableLiveData<CenticBidsUser>()
        val firebaseUser = firebaseAuth.currentUser

        var user = CenticBidsUser()

        if (firebaseUser == null) {
            user.isAuthenticated = false
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user)
        } else {
            user.userId = firebaseUser.uid
            user.email = firebaseUser.email
            user.isAuthenticated = true
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user)
        }
        return isUserAuthenticateInFirebaseMutableLiveData
    }

    //update the users fcm token on each sign in, so that the users fcm token is always upto date  even if logged in from a different device
    fun updateUserFcmTokenOnLogin(userId : String, fcmToken : String) : MutableLiveData<Boolean>? {

        var updatedTokenSuccessfully : Boolean = false

        val auctionBidUpdateMutableLiveData = MutableLiveData<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {

            val updateBidQuery = usersCollectionRef
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if(updateBidQuery.documents.isNotEmpty()) {

                for(document in updateBidQuery){

                    try {

                        usersCollectionRef.document(document.id).update("fcm_token", fcmToken).await()
                        updatedTokenSuccessfully = true
                        auctionBidUpdateMutableLiveData.postValue(updatedTokenSuccessfully)

                    }catch (e: Exception){

                        updatedTokenSuccessfully = false
                        auctionBidUpdateMutableLiveData.postValue(updatedTokenSuccessfully)

                    }

                }

            }

        }

        return auctionBidUpdateMutableLiveData

    }



}
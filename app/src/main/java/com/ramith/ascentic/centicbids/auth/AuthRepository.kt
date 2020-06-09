package com.ramith.ascentic.centicbids.auth

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
import kotlinx.coroutines.withContext


class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreDbRef = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestoreDbRef.collection("registered_users")

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
                    withContext(Dispatchers.Main){
                        //Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }

        }

        return authenticatedUserMutableLiveData
    }

    fun registerUser(email : String, password : String): MutableLiveData<CenticBidsUser>? {

        val registeredUserMutableLiveData = MutableLiveData<CenticBidsUser>()

        if(email.isNotEmpty() && password.isNotEmpty()){

            CoroutineScope(Dispatchers.IO).launch {

                try{

                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                    val firebaseUser = firebaseAuth.currentUser
                    if(firebaseUser == null){
                        //user not logged in
                        Log.d("BIDS_AUTH", "user NOT registered")
                    }else {
                        //user is logged in
                        Log.d("BIDS_AUTH", "user is registered")
                        val uid: String = firebaseUser.getUid()
                        val email: String = firebaseUser.getEmail()!!

                        val user = CenticBidsUser()
                        user.userId = uid
                        user.email = email
                        user.isAuthenticated = true
                        registeredUserMutableLiveData.postValue(user)
                    }
                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        //Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }

        }

        return registeredUserMutableLiveData
    }

    fun createUserInFirestoreIfNotExists(authenticatedUser: CenticBidsUser): MutableLiveData<CenticBidsUser>? {

        val newUserMutableLiveData = MutableLiveData<CenticBidsUser>()
        val uidRef: DocumentReference = usersCollectionRef.document(authenticatedUser.userId!!)

        uidRef.get().addOnCompleteListener { uidTask: Task<DocumentSnapshot?> ->

                if (uidTask.isSuccessful) {

                    val document = uidTask.result

                    if (!document!!.exists()) {

                        uidRef.set(authenticatedUser).addOnCompleteListener { userCreationTask: Task<Void?> ->

                                if (userCreationTask.isSuccessful) {
                                    newUserMutableLiveData.setValue(authenticatedUser)
                                } else {
                                    //logErrorMessage(userCreationTask.exception!!.message)
                                }
                            }
                    } else {
                        newUserMutableLiveData.setValue(authenticatedUser)
                    }

                } else {
                    //logErrorMessage(uidTask.exception!!.message)
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
            isUserAuthenticateInFirebaseMutableLiveData.postValue(user)
        } else {
            user.userId = firebaseUser.uid
            user.email = firebaseUser.email
            user.isAuthenticated = true
            isUserAuthenticateInFirebaseMutableLiveData.postValue(user)
        }
        return isUserAuthenticateInFirebaseMutableLiveData
    }



}
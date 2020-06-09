package com.ramith.ascentic.centicbids.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ramith.ascentic.centicbids.model.CenticBidsUser


class AuthViewModel(application: Application?) : AndroidViewModel(application!!) {

    var authRepository: AuthRepository = AuthRepository()

    var authenticatedUserLiveData: LiveData<CenticBidsUser>? = null
    var createdUserLiveData: LiveData<CenticBidsUser>? = null
    var isUserAuthenticatedLiveData: LiveData<CenticBidsUser>? = null

    fun loginUserWithEmailAndPassword(email : String, password : String) {

        authenticatedUserLiveData = authRepository.loginUser(email, password)

    }

    fun registerUser(email : String, password : String) {

        authenticatedUserLiveData = authRepository.registerUser(email, password)

    }

    fun createUser(authenticatedUser: CenticBidsUser?) {

        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser!!)

    }

    fun isUserAuthenticated(){

        isUserAuthenticatedLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase()

    }

}
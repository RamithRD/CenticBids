package com.ramith.ascentic.centicbids.feature.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ramith.ascentic.centicbids.model.CenticBidsUser


class AuthViewModel() : ViewModel() {

    var authRepository: AuthRepository =
        AuthRepository()

    var authenticatedUserLiveData: LiveData<CenticBidsUser>? = null
    var createdUserLiveData: LiveData<CenticBidsUser>? = null
    var isUserAuthenticatedLiveData: LiveData<CenticBidsUser>? = null
    var updatedFcmTokenLiveData: LiveData<Boolean>? = null

    fun loginUserWithEmailAndPassword(email : String, password : String) {

        authenticatedUserLiveData = authRepository.loginUser(email, password)

    }

    fun registerUser(email : String, password : String, fcmToken : String) {

        authenticatedUserLiveData = authRepository.registerUser(email, password, fcmToken)

    }

    fun createUser(authenticatedUser: CenticBidsUser?) {

        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser!!)

    }

    fun isUserAuthenticated(){

        isUserAuthenticatedLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase()

    }

    fun updateFcmTokenOnLogin(userId : String, fcmToken : String){

        updatedFcmTokenLiveData = authRepository.updateUserFcmTokenOnLogin(userId, fcmToken)

    }

}
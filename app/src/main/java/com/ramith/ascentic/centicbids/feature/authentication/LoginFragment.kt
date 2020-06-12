package com.ramith.ascentic.centicbids.feature.authentication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.ramith.ascentic.centicbids.R
import com.ramith.ascentic.centicbids.utils.EmailAddressValidator
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.indeterminateProgressDialog

/**
 * Login fragment is responsible for authenticating an already registererd user with Firebase
 */
class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var authViewModel : AuthViewModel

    lateinit var firebaseAuth : FirebaseAuth

    var fcmToken : String = ""

    lateinit var emailFormatValidator : EmailAddressValidator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        emailFormatValidator = EmailAddressValidator()
        loginEmailEdt.addTextChangedListener(emailFormatValidator)

        getFcmToken()

        signUpTxt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginUserBtn.setOnClickListener {

            if (!emailFormatValidator.isValid()) {
                loginEmailEdt.error = "Invalid email address"
                return@setOnClickListener;
            }

            val email = loginEmailEdt.text.toString()
            val password = loginPasswordEdt.text.toString()

            if (password.isEmpty()) {
                loginPasswordEdt.error = "Password cannot be empty"
                return@setOnClickListener;
            }

            if (password.length < 6) {
                loginPasswordEdt.error = "Password cannot be less than 6 characters"
                return@setOnClickListener;
            }

            //signing in user
            val progressDialog = activity?.indeterminateProgressDialog(message = "Signing into CenticBids...", title = "CenticBids")
            authViewModel.loginUserWithEmailAndPassword(email, password)
            authViewModel.authenticatedUserLiveData!!.observe(viewLifecycleOwner, Observer { authenticatedUser ->

                if(authenticatedUser.isAuthenticated){
                    Log.d("BIDS_AUTH", "user has email")
                    updateFcmTokenOnLogin(authenticatedUser.userId.toString())
                    progressDialog?.dismiss()
                } else {
                    Toast.makeText(activity, authenticatedUser.statusMsg, Toast.LENGTH_LONG).show()
                    progressDialog?.dismiss()
                }

            })
        }

    }

    //updates the users fcm token field in db so the user can receive outbid notifications
    private fun updateFcmTokenOnLogin(userId : String){

        //hiding keyboard if it has focus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)

        authViewModel.updateFcmTokenOnLogin(userId, fcmToken)
        authViewModel.updatedFcmTokenLiveData!!.observe(viewLifecycleOwner, Observer {

            findNavController().navigate(R.id.action_loginFragment_to_auctionsListFragment)

        })

    }


    private fun checkedLoggedInState() {

        authViewModel.isUserAuthenticated()
        authViewModel.isUserAuthenticatedLiveData!!.observe(viewLifecycleOwner, Observer { userObj ->

            if(userObj.isAuthenticated){
                Toast.makeText(activity, "USER LOGGED IN", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_loginFragment_to_auctionsListFragment)
            } else {

            }

        })


    }

    //gets the fcm token of the current device
    private fun getFcmToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(OnCompleteListener  { fcmTask ->

            if (!fcmTask.isSuccessful) {
                Log.d("REG_USER", "getInstanceId failed", fcmTask.exception)
                return@OnCompleteListener
            }

            fcmToken = fcmTask.result?.token.toString()
            Log.d("REG_USER", fcmToken, fcmTask.exception)


        })

    }



    override fun onStart() {
        super.onStart()
        //check if there's a logged in user
        checkedLoggedInState()
    }


}

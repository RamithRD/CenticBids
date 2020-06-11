package com.ramith.ascentic.centicbids.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.ramith.ascentic.centicbids.R
import com.ramith.ascentic.centicbids.model.CenticBidsUser
import com.ramith.ascentic.centicbids.utils.EmailAddressValidator
import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.indeterminateProgressDialog

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment(R.layout.fragment_register) {

    lateinit var authViewModel : AuthViewModel

    lateinit var firebaseAuth : FirebaseAuth

    var fcmToken : String = ""

    lateinit var emailFormatValidator : EmailAddressValidator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        emailFormatValidator = EmailAddressValidator()
        registerEmailEdt.addTextChangedListener(emailFormatValidator)

        getFcmToken()

        backToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        registerUserBtn.setOnClickListener {

            if (!emailFormatValidator.isValid()) {
                registerEmailEdt.error = "Invalid email address"
                return@setOnClickListener;
            }

            val email = registerEmailEdt.text.toString()
            val password = registerPasswordEdt.text.toString()

            if (password.isEmpty()) {
                registerPasswordEdt.error = "Password cannot be empty"
                return@setOnClickListener;
            }

            if (password.length < 6) {
                registerPasswordEdt.error = "Password cannot be less than 6 characters"
                return@setOnClickListener;
            }

            val progressDialog = activity?.indeterminateProgressDialog(message = "Signing up with CenticBids...", title = "CenticBids")

            authViewModel.registerUser(email, password, fcmToken)
            authViewModel.authenticatedUserLiveData!!.observe(viewLifecycleOwner, Observer { authenticatedUser ->


                if(authenticatedUser.isAuthenticated){
                    Log.d("BIDS_AUTH", "user has email")
                    createUserRecordInFirestore(authenticatedUser)
                    progressDialog?.dismiss()

                } else {
                    Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show()
                }

            })

        }

    }

    private fun createUserRecordInFirestore(user : CenticBidsUser){

        authViewModel.createUser(user)
        authViewModel.createdUserLiveData!!.observe(viewLifecycleOwner, Observer {

            findNavController().navigate(R.id.action_registerFragment_to_auctionsListFragment)

        })

    }

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

    override fun onStart() {
        super.onStart()
        //check if there's a logged in user
        checkedLoggedInState()
    }


}

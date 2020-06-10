package com.ramith.ascentic.centicbids.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.ramith.ascentic.centicbids.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var authViewModel : AuthViewModel

    lateinit var firebaseAuth : FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)


        //firebaseAuth.signOut()

        signUpTxt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginUserBtn.setOnClickListener {
            //loginUser()
            val email = loginEmailEdt.text.toString()
            val password =loginPasswordEdt.text.toString()

            val progressDialog = activity?.indeterminateProgressDialog(message = "Signing into CenticBids...", title = "CenticBids")
            authViewModel.loginUserWithEmailAndPassword(email, password)
            authViewModel.authenticatedUserLiveData!!.observe(viewLifecycleOwner, Observer { authenticatedUser ->

                progressDialog?.dismiss()
                Log.d("BIDS_AUTH", "user has email")

//                    if (authenticatedUser.isNew) {
//                        createNewUser(authenticatedUser)
//                    } else {
//                        goToMainActivity(authenticatedUser)
//                    }

                    if(authenticatedUser.isAuthenticated){
                        Log.d("BIDS_AUTH", "user has email")
                        findNavController().navigate(R.id.action_loginFragment_to_auctionsListFragment)
                    } else {
                        Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show()
                    }

            })
        }

    }


    private fun checkedLoggedInState() {

        authViewModel.isUserAuthenticated()
        authViewModel.isUserAuthenticatedLiveData!!.observe(viewLifecycleOwner, Observer { userObj ->

            if(userObj.isAuthenticated){
                Toast.makeText(activity, "USER LOGGED IN", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_loginFragment_to_auctionsListFragment)
            } else {
                Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show()
            }

        })


    }



    override fun onStart() {
        super.onStart()
        //check if there's a logged in user
        checkedLoggedInState()
    }


}
